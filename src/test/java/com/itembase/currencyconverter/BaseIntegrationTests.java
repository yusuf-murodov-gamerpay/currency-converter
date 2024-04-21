package com.itembase.currencyconverter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public abstract class BaseIntegrationTests {
    protected static final WireMockServer WIRE_MOCK = new WireMockServer(
            WireMockSpring.options().dynamicPort().extensions(new ResponseTemplateTransformer(false)));

    static {
        WIRE_MOCK.start();
    }

    protected static WebTestClient.ResponseSpec doPost(WebTestClient webTestClient, String url, Object body) {
        return webTestClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }
}
