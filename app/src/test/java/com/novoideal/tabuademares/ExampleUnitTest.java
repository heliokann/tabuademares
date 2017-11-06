package com.novoideal.tabuademares;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void latLongCaboFrio() throws Exception {
        final Geocoder geocoder = new Geocoder();
        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress("Cabo Frio, Rio de Janeiro").setLanguage("pt-BR").getGeocoderRequest();
        GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

        LatLng latLong = geocoderResponse.getResults().get(0).getGeometry().getLocation();

        assertEquals(-22.89, latLong.getLat().doubleValue(), 0.01);
        assertEquals(-42.03, latLong.getLng().doubleValue(), 0.01);
    }
}