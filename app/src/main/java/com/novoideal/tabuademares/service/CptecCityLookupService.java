package com.novoideal.tabuademares.service;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CptecCityLookupService {

    private static final String LOOKUP_URL = "http://servicos.cptec.inpe.br/XML/listaCidades?city=";

    public int lookupCode(String cityName) {
        try {
            String encoded = URLEncoder.encode(stripState(cityName), "UTF-8");
            String urlStr = LOOKUP_URL + encoded;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(8000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream stream = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            CityHandler handler = new CityHandler();
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(new InputSource(buffer));
            stream.close();

            return handler.getFirstId();
        } catch (Exception e) {
            return 0;
        }
    }

    String stripState(String nameWithState) {
        int idx = nameWithState.indexOf(" - ");
        return idx >= 0 ? nameWithState.substring(0, idx) : nameWithState;
    }

    private static class CityHandler extends DefaultHandler {
        private int firstId = 0;
        private String currentText;
        private boolean foundFirst = false;

        public int getFirstId() {
            return firstId;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (!foundFirst && "id".equals(localName)) {
                try {
                    firstId = Integer.parseInt(currentText.trim());
                    foundFirst = true;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            currentText = new String(ch, start, length);
        }
    }
}
