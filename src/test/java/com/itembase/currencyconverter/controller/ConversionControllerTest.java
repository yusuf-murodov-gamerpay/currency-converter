package com.itembase.currencyconverter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = ConversionController.class)
class ConversionControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void convert_shouldReturn200() {

    }
}
