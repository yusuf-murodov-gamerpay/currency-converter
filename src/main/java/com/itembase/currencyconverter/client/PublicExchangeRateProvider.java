package com.itembase.currencyconverter.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itembase.currencyconverter.constant.ExchangeRateConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublicExchangeRateProvider implements ExchangeRateProvider {
    private final WebClient publicExchangeRateProviderWebClient;

    @Override
    public Mono<Double> getExchangeRate(final String base, final String convertTo) {
        log.debug("Fetching exchange rate for {} to {}", base, convertTo);
        return publicExchangeRateProviderWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{currency}").build(base))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response -> processResponse(response, convertTo))
                .onErrorResume(JsonProcessingException.class, this::handleJsonProcessingException);
    }

    private Mono<Double> processResponse(final JsonNode response, final String convertTo) {
        if (isSuccessResponse(response)) {
            return Mono.justOrEmpty(getRateValue(response, convertTo));
        } else {
            log.error("Error while fetching exchange rate for {}", convertTo);
            return Mono.error(new IllegalStateException("Error while fetching exchange rate for " + convertTo));
        }
    }

    private double getRateValue(final JsonNode response, final String convertTo) {
        final JsonNode rates = response.path(ExchangeRateConstant.RATES_FIELD);
        return rates.path(convertTo).asDouble();
    }

    private boolean isSuccessResponse(final JsonNode response) {
        final String result = response.path(ExchangeRateConstant.RESULT_FIELD).asText();
        return result == null || !result.equals("error");
    }

    private Mono<Double> handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error while processing response", e);
        return Mono.error(new IllegalStateException("Error while processing response", e));
    }
}
