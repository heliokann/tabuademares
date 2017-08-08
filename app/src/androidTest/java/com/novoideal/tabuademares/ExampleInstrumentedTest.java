package com.novoideal.tabuademares;

import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

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

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.novoideal.tabuademares", appContext.getPackageName());

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

            handler.getResult();

        } catch (SAXException exception) {
            throw new IOException("Error parsing DynamoDB Local manifest file", exception);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


    }

    class SwelltHandler extends DefaultHandler {
        private final List<Entry> data = new ArrayList<Entry>();

        private String currentText;
        private Entry current;

        private String city;
        private String uf;
        private String updated;

        public List<Entry> getResult() {
            return data;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {

            switch (localName) {
                case "manha":
                case "tarde":
                case "noite":
                    current = new Entry();
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
                    current.setDate(new Date());
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
                    current.setWind(currentText);
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

    class Entry {
        private String cidade;
        private String uf;
        private String updated;
        private String period;
        private Date date;
        private String agitation;
        private Double height;
        private String  sewll;
        private String wind;
        private String wind_dir;

        public Entry() {

        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getAgitation() {
            return agitation;
        }

        public void setAgitation(String agitation) {
            this.agitation = agitation;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public String getSewll() {
            return sewll;
        }

        public void setSewll(String sewll) {
            this.sewll = sewll;
        }

        public String getWind() {
            return wind;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getWind_dir() {
            return wind_dir;
        }

        public void setWind_dir(String wind_dir) {
            this.wind_dir = wind_dir;
        }
    }
}
