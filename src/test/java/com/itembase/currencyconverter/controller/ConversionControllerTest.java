package com.itembase.currencyconverter.controller;

import com.itembase.currencyconverter.BaseIntegrationTests;
import com.itembase.currencyconverter.TestUtil;
import com.itembase.currencyconverter.domain.dto.ConversionResponse;
import com.itembase.currencyconverter.domain.dto.Error;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.itembase.currencyconverter.TestConstants.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConversionControllerTest extends BaseIntegrationTests {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void convert_shouldReturn200() {
        final String body = TestUtil.readFileContentFromResource(CONVERSION_REQUEST_PATH);

        doPost(webTestClient, CONVERSION_URL, body)
                .expectStatus().isOk()
                .expectBody(ConversionResponse.class)
                .value(ConversionResponse::getFrom, equalTo("EUR"))
                .value(ConversionResponse::getTo, equalTo("USD"))
                .value(ConversionResponse::getConverted, Matchers.notNullValue());
    }

    @Test
    void convert_shouldReturn400_whenInvalidCurrency() {
        final String body = TestUtil.readFileContentFromResource(CONVERSION_WRONG_REQUEST_1_PATH);

        doPost(webTestClient, CONVERSION_URL, body)
                .expectStatus().isBadRequest();
    }

    @Test
    void convert_shouldReturn400_whenInvalidFormatField() {
        final String body = TestUtil.readFileContentFromResource(CONVERSION_WRONG_REQUEST_2_PATH);

        doPost(webTestClient, CONVERSION_URL, body)
                .expectStatus().isBadRequest();
    }
}
