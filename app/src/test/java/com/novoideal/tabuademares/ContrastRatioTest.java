package com.novoideal.tabuademares;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Verifies WCAG AA contrast ratios (≥ 4.5:1) for the Settings screen color pairs.
 * Colors are sourced from res/values/colors.xml and res/values-night/colors.xml.
 */
public class ContrastRatioTest {

    private static double relativeLuminance(int color) {
        double r = linearize(((color >> 16) & 0xFF) / 255.0);
        double g = linearize(((color >> 8) & 0xFF) / 255.0);
        double b = (color & 0xFF) / 255.0;
        b = linearize(b);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    private static double linearize(double c) {
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    private static double contrastRatio(int fg, int bg) {
        double l1 = relativeLuminance(fg);
        double l2 = relativeLuminance(bg);
        double lighter = Math.max(l1, l2);
        double darker  = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static int hex(String s) {
        return (int) Long.parseLong(s.replace("#", ""), 16);
    }

    // ── Light mode ────────────────────────────────────────────────────────────

    @Test
    public void lightMode_titleOnSurface_meetsWCAG_AA() {
        // colorOnSurface (#1A1A2E) on colorSurface (#FFFFFF)
        double ratio = contrastRatio(hex("#1A1A2E"), hex("#FFFFFF"));
        assertTrue("Light title contrast " + ratio + " < 4.5", ratio >= 4.5);
    }

    @Test
    public void lightMode_summaryOnSurface_meetsWCAG_AA() {
        // colorSecondaryText (#6B7280) on colorSurface (#FFFFFF)
        double ratio = contrastRatio(hex("#6B7280"), hex("#FFFFFF"));
        assertTrue("Light summary contrast " + ratio + " < 4.5", ratio >= 4.5);
    }

    // ── Dark mode ─────────────────────────────────────────────────────────────

    @Test
    public void darkMode_titleOnSurface_meetsWCAG_AA() {
        // colorOnSurface night (#E8EAF0) on colorSurface night (#1E1E1E)
        double ratio = contrastRatio(hex("#E8EAF0"), hex("#1E1E1E"));
        assertTrue("Dark title contrast " + ratio + " < 4.5", ratio >= 4.5);
    }

    @Test
    public void darkMode_summaryOnSurface_meetsWCAG_AA() {
        // colorSecondaryText night (#9CA3AF) on colorSurface night (#1E1E1E)
        double ratio = contrastRatio(hex("#9CA3AF"), hex("#1E1E1E"));
        assertTrue("Dark summary contrast " + ratio + " < 4.5", ratio >= 4.5);
    }

    // ── Dialog ────────────────────────────────────────────────────────────────

    @Test
    public void lightMode_dialogTextOnDialogBackground_meetsWCAG_AA() {
        // Default Material dialog: #1A1A2E on #FFFFFF surface
        double ratio = contrastRatio(hex("#1A1A2E"), hex("#FFFFFF"));
        assertTrue("Dialog text contrast " + ratio + " < 4.5", ratio >= 4.5);
    }

    @Test
    public void darkMode_dialogTextOnDialogBackground_meetsWCAG_AA() {
        // Dark Material dialog: #E8EAF0 on #1E1E1E surface
        double ratio = contrastRatio(hex("#E8EAF0"), hex("#1E1E1E"));
        assertTrue("Dark dialog text contrast " + ratio + " < 4.5", ratio >= 4.5);
    }
}
