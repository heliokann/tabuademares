package com.novoideal.tabuademares.service;

import com.novoideal.tabuademares.service.CptecCityLookupService;

import org.junit.Test;

import static org.junit.Assert.*;

public class CptecCityLookupServiceTest {

    private final CptecCityLookupService service = new CptecCityLookupService();

    @Test
    public void stripState_removesStateSuffix() {
        assertEquals("Cabo Frio", service.stripState("Cabo Frio - RJ"));
    }

    @Test
    public void stripState_noSuffix_returnsOriginal() {
        assertEquals("Cabo Frio", service.stripState("Cabo Frio"));
    }

    @Test
    public void stripState_emptyString() {
        assertEquals("", service.stripState(""));
    }

    @Test
    public void stripState_multipleHyphens_removesOnlyFirst() {
        assertEquals("São Luís", service.stripState("São Luís - MA"));
    }

    @Test
    public void lookupCode_unknownCity_returnsZero() {
        // Network call for a clearly invalid city name — should return 0 gracefully
        int code = service.lookupCode("XxXCidadeInexistenteXxX");
        assertEquals(0, code);
    }
}
