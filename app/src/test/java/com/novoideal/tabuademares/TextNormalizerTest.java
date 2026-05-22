package com.novoideal.tabuademares;

import com.novoideal.tabuademares.util.TextNormalizer;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextNormalizerTest {

    @Test
    public void normalize_removesAccents() {
        assertEquals("florianopolis", TextNormalizer.normalize("Florianópolis"));
    }

    @Test
    public void normalize_isLowercase() {
        assertEquals("sao paulo", TextNormalizer.normalize("SÃO PAULO"));
    }

    @Test
    public void normalize_handlesMultipleAccents() {
        assertEquals("aguas de lindoia", TextNormalizer.normalize("Águas de Lindóia"));
    }

    @Test
    public void normalize_handlesPlainText() {
        assertEquals("recife", TextNormalizer.normalize("Recife"));
    }

    @Test
    public void normalize_emptyString() {
        assertEquals("", TextNormalizer.normalize(""));
    }

    @Test
    public void normalize_cedilha() {
        assertEquals("angra dos reis", TextNormalizer.normalize("Angra dos Reis"));
        assertEquals("aracaju", TextNormalizer.normalize("Aracaju"));
    }

    @Test
    public void normalize_tilde() {
        assertEquals("joao pessoa", TextNormalizer.normalize("João Pessoa"));
        assertEquals("belem", TextNormalizer.normalize("Belém"));
    }
}
