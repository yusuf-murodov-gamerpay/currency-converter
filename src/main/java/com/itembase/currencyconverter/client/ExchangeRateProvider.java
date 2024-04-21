package com.itembase.currencyconverter.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
public abstract class ExchangeRateProvider {
    private final WebClient webClient;

    protected abstract URI getExchangeRateUri(final UriBuilder uriBuilder, final String base, final String convertTo);

    protected abstract Mono<Double> processResponse(final JsonNode response, final String convertTo);

    public Mono<Double> getExchangeRate(final String base, final String convertTo) {
        log.debug("Fetching exchange rate for {} to {}", base, convertTo);
        return webClient.get()
                .uri(uriBuilder -> getExchangeRateUri(uriBuilder, base, convertTo))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> processResponse(response, convertTo))
                .onErrorResume(JsonProcessingException.class, this::handleJsonProcessingException);
    }

    private Mono<Double> handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error while processing response", e);
        return Mono.error(e);
    }
}
