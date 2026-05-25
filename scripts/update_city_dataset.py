#!/usr/bin/env python3
"""
update_city_dataset.py

Discovers all coastal cities from tabuademares.com and merges them into
app/src/main/assets/cidades_litoraneas.json.

Rules:
  - Existing cities WITH tabuademaresPath: kept as-is (preserves cptecCode).
  - Existing cities WITHOUT tabuademaresPath: kept as-is; if a match is found
    on the site by normalised name+state, the path is patched in.
  - New cities discovered on the site: added with coordinates from the page.
  - Result sorted by state then name; minor version bumped.

Usage:
    python scripts/update_city_dataset.py [--dry-run]
"""

import argparse
import json
import os
import re
import sys
import time
import unicodedata
import urllib.error
import urllib.request
from html.parser import HTMLParser

BASE_URL = "https://tabuademares.com"
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
JSON_PATH = os.path.join(SCRIPT_DIR, "..", "app", "src", "main", "assets", "cidades_litoraneas.json")

STATE_SLUG_TO_UF: dict[str, str] = {
    "amapa":              "AP",
    "para":               "PA",
    "maranhao":           "MA",
    "piaui":              "PI",
    "ceara":              "CE",
    "rio-grande-do-norte":"RN",
    "paraiba":            "PB",
    "pernambuco":         "PE",
    "alagoas":            "AL",
    "sergipe":            "SE",
    "bahia":              "BA",
    "espirito-santo":     "ES",
    "rio-de-janeiro":     "RJ",
    "so-paulo":           "SP",
    "parana":             "PR",
    "santa-catarina":     "SC",
    "rio-grande-do-sul":  "RS",
}


# ── HTTP ──────────────────────────────────────────────────────────────────────

def fetch(url: str) -> str:
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "Mozilla/5.0 (compatible; tabuademares-dataset-updater/1.0)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            return resp.read().decode("utf-8", errors="replace")
    except urllib.error.URLError as e:
        print(f"  [WARN] fetch failed: {url} — {e}", file=sys.stderr)
        return ""


# ── DMS → decimal ─────────────────────────────────────────────────────────────

def parse_dms(text: str) -> float | None:
    """
    Converts a DMS string like '21° 24' 01" S' to decimal degrees.
    Returns negative for S/W directions.
    """
    direction = None
    for d in ("N", "S", "E", "W"):
        if d in text:
            direction = d
            break
    if direction is None:
        return None
    nums = re.findall(r"\d+", text)
    if len(nums) < 3:
        return None
    deg, mins, secs = int(nums[0]), int(nums[1]), int(nums[2])
    decimal = deg + mins / 60.0 + secs / 3600.0
    if direction in ("S", "W"):
        decimal = -decimal
    return round(decimal, 6)


# ── HTML parser ───────────────────────────────────────────────────────────────

class CityParser(HTMLParser):
    """
    Parses a tabuademares.com state page and extracts city entries.

    Relevant HTML structure (one entry):
        <a class="sitio_estacion_a" href="https://tabuademares.com/br/{state}/{city}" title="marés de {City}">
          <div class="sitio_estacion">
            <img class="sitio_estacion_icono" ...>
            <div>{City Name}</div>
            <div class="sitio_estacion_coordenadas">
              <span class="sitio_estacion_coordenadas_N">DD° MM' SS" <strong>S</strong></span>
              <span class="sitio_estacion_coordenadas_W">DD° MM' SS" <strong>W</strong></span>
            </div>
          </div>
        </a>
    """

    def __init__(self, state_slug: str):
        super().__init__()
        self.state_slug = state_slug
        self.cities: list[dict] = []

        # state per entry
        self._cur: dict | None = None
        self._in_estacion_div = False  # inside div.sitio_estacion
        self._estacion_div_depth = 0
        self._in_name_div = False       # inside the nameless city-name <div>
        self._in_coord_n = False        # inside span.sitio_estacion_coordenadas_N
        self._in_coord_w = False        # inside span.sitio_estacion_coordenadas_W
        self._coord_buf: list[str] = []

    # helpers
    @staticmethod
    def _classes(attrs: list) -> list[str]:
        return dict(attrs).get("class", "").split()

    def handle_starttag(self, tag, attrs):
        cls = self._classes(attrs)
        a = dict(attrs)

        if tag == "a" and "sitio_estacion_a" in cls:
            href = a.get("href", "")
            # Match both absolute and relative hrefs
            m = re.search(
                rf"/br/{re.escape(self.state_slug)}/([^/\s\"']+)",
                href,
            )
            if m:
                slug = m.group(1)
                self._cur = {
                    "tabuademaresPath": f"/br/{self.state_slug}/{slug}",
                    "name": None,
                    "lat": None,
                    "lng": None,
                }
                self._in_estacion_div = False
                self._estacion_div_depth = 0
                self._in_name_div = False

        elif tag == "div" and self._cur is not None:
            if "sitio_estacion" in cls and "sitio_estacion_coordenadas" not in cls:
                # Entering div.sitio_estacion
                self._in_estacion_div = True
                self._estacion_div_depth = 1

            elif self._in_estacion_div:
                self._estacion_div_depth += 1
                # The nameless div (no class) at depth 2 is the city name container
                if self._estacion_div_depth == 2 and not a.get("class"):
                    self._in_name_div = True

        elif tag == "span" and self._cur is not None:
            if "sitio_estacion_coordenadas_N" in cls:
                self._in_coord_n = True
                self._coord_buf = []
            elif "sitio_estacion_coordenadas_W" in cls:
                self._in_coord_w = True
                self._coord_buf = []

    def handle_endtag(self, tag):
        if tag == "a" and self._cur is not None:
            if self._cur.get("name") and self._cur.get("lat") is not None:
                self.cities.append(self._cur)
            self._cur = None
            self._in_estacion_div = False
            self._in_name_div = False

        elif tag == "div" and self._in_estacion_div:
            if self._in_name_div:
                self._in_name_div = False
            self._estacion_div_depth -= 1
            if self._estacion_div_depth <= 0:
                self._in_estacion_div = False

        elif tag == "span":
            buf = "".join(self._coord_buf)
            if self._in_coord_n:
                self._in_coord_n = False
                self._cur["lat"] = parse_dms(buf)
            elif self._in_coord_w:
                self._in_coord_w = False
                self._cur["lng"] = parse_dms(buf)
            self._coord_buf = []

    def handle_data(self, data):
        if self._cur is None:
            return
        text = data.strip()
        if not text:
            return
        if self._in_name_div:
            if self._cur["name"] is None:
                self._cur["name"] = text
        elif self._in_coord_n or self._in_coord_w:
            self._coord_buf.append(data)


# ── Discovery ─────────────────────────────────────────────────────────────────

def discover_cities() -> list[dict]:
    all_cities: list[dict] = []
    slugs = list(STATE_SLUG_TO_UF.keys())
    print(f"Scraping {len(slugs)} states from tabuademares.com …")

    for slug in slugs:
        uf = STATE_SLUG_TO_UF[slug]
        print(f"  [{uf}] {slug} … ", end="", flush=True)
        html = fetch(f"{BASE_URL}/br/{slug}")
        time.sleep(0.8)
        if not html:
            print("FAILED")
            continue
        parser = CityParser(slug)
        parser.feed(html)
        valid = [c for c in parser.cities if c["lat"] is not None and c["lng"] is not None]
        print(f"{len(valid)} cities")
        for c in valid:
            c["state"] = uf
            all_cities.append(c)

    return all_cities


# ── Normalisation & merge ─────────────────────────────────────────────────────

def normalise(name: str) -> str:
    nfkd = unicodedata.normalize("NFKD", name)
    return "".join(c for c in nfkd if not unicodedata.combining(c)).lower().strip()


def in_brazil(lat, lng) -> bool:
    if lat is None or lng is None:
        return False
    return -35.0 <= lat <= 6.0 and -75.0 <= lng <= -25.0


def merge(
    existing: list[dict], discovered: list[dict]
) -> tuple[list[dict], int, int]:
    by_path: dict[str, dict] = {
        c["tabuademaresPath"]: c for c in existing if c.get("tabuademaresPath")
    }
    by_name_state: dict[tuple, dict] = {
        (normalise(c["name"]), c["state"]): c
        for c in existing
        if not c.get("tabuademaresPath")
    }

    result = list(existing)
    added = patched = 0

    for city in discovered:
        path = city["tabuademaresPath"]

        if path in by_path:
            continue  # already known

        key = (normalise(city["name"]), city["state"])
        if key in by_name_state:
            by_name_state[key]["tabuademaresPath"] = path
            patched += 1
            continue

        if not in_brazil(city.get("lat"), city.get("lng")):
            print(f"  [WARN] {city['name']} ({city['state']}) — coords out of bounds, skipped")
            continue

        entry = {
            "name":             city["name"],
            "state":            city["state"],
            "lat":              city["lat"],
            "lng":              city["lng"],
            "cptecCode":        0,
            "tabuademaresPath": path,
        }
        result.append(entry)
        by_path[path] = entry
        added += 1

    return result, added, patched


def sort_cities(cities: list[dict]) -> list[dict]:
    return sorted(cities, key=lambda c: (c["state"], normalise(c["name"])))


def bump_version(v: str) -> str:
    parts = v.split(".")
    try:
        return f"{parts[0]}.{int(parts[-1]) + 1}"
    except (IndexError, ValueError):
        return v


# ── Main ──────────────────────────────────────────────────────────────────────

def main():
    ap = argparse.ArgumentParser(
        description="Update cidades_litoraneas.json from tabuademares.com"
    )
    ap.add_argument("--dry-run", action="store_true", help="Preview without writing")
    args = ap.parse_args()

    with open(JSON_PATH, encoding="utf-8") as f:
        data = json.load(f)

    existing: list[dict] = data["cities"]
    version: str = data.get("version", "1.1")
    print(f"Loaded {len(existing)} existing cities (v{version})\n")

    discovered = discover_cities()
    print(f"\nDiscovered {len(discovered)} cities with coordinates\n")

    merged, added, patched = merge(existing, discovered)
    merged = sort_cities(merged)

    print(f"Result: {len(merged)} total  ({added} new added, {patched} paths patched, {len(existing)} preserved)")

    if args.dry_run:
        orig_paths = {c.get("tabuademaresPath", "") for c in existing}
        new_entries = [
            c for c in merged
            if c.get("tabuademaresPath") and c["tabuademaresPath"] not in orig_paths
        ]
        print(f"\n-- DRY RUN: {len(new_entries)} cities that would be added --")
        for c in sorted(new_entries, key=lambda x: (x["state"], x["name"])):
            print(f"  [{c['state']}] {c['name']:<35} {c['tabuademaresPath']}")
        return

    new_version = bump_version(version)
    output = {"version": new_version, "cities": merged}

    with open(JSON_PATH, "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=2)
        f.write("\n")

    print(f"\nWritten {JSON_PATH}  (v{version} -> v{new_version})")


if __name__ == "__main__":
    main()
