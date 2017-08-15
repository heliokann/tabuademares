package com.novoideal.tabuademares.service;

import com.novoideal.tabuademares.model.Weather;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Helio on 08/08/2017.
 */

public class XmlWeatherService {

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public List<Weather> getWeathers(String xmlUrl) throws Exception {
        InputStream stream = null;
        try {

            stream = downloadUrl("http://servicos.cptec.inpe.br/XML/cidade/241/dia/0/ondas.xml");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

            SwelltHandler handler = new SwelltHandler();
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(new InputSource(buffer));

            return handler.getResult();

        } catch (SAXException exception) {
            throw new IOException("Error parsing DynamoDB Local manifest file", exception);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


    }

    class SwelltHandler extends DefaultHandler {
        private final List<Weather> data = new ArrayList<>();

        private String currentText;
        private Weather current;

        private String city;
        private String uf;
        private String updated;

        public List<Weather> getResult() {
            return data;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {

            switch (localName) {
                case "manha":
                case "tarde":
                case "noite":
                    current = new Weather();
                    current.setPeriod(localName);
                    current.setCidade(city);
                    current.setUf(uf);
                    current.setUpdated(updated);
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {

            switch (localName) {
                case "cidade":
                    break;
                case "nome":
                    city = currentText;
                    break;
                case "uf":
                    uf = currentText;
                    break;
                case "atualizacao":
                    updated = currentText;
                    break;
                case "manha":
                case "tarde":
                case "noite":
                    data.add(current);
                    break;
                case "dia":
                    current.setDate(currentText);
                    break;
                case "agitacao":
                    current.setAgitation(currentText);
                    break;
                case "altura":
                    current.setHeight(Double.parseDouble(currentText));
                    break;
                case "direcao":
                    current.setSewll(currentText);
                    break;
                case "vento":
                    current.setWind(Double.parseDouble(currentText));
                    break;
                case "vento_dir":
                    current.setWind_dir(currentText);
                    break;
                default:
            }

        }

        @Override
        public void characters(final char[] ch, final int start, final int length) {
            currentText = new String(ch);
        }
    }
}
