package com.itembase.currencyconverter.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.itembase.currencyconverter.constant.ExchangeRateConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class PublicExchangeRateProvider extends ExchangeRateProvider {

    public PublicExchangeRateProvider(WebClient publicExchangeRateProviderWebClient) {
        super(publicExchangeRateProviderWebClient);
    }

    @Override
    protected URI getExchangeRateUri(UriBuilder uriBuilder, String base, String convertTo) {
        return uriBuilder.path("/{currency}").build(base);
    }

    protected Mono<Double> processResponse(final JsonNode response, final String convertTo) {
        if (isSuccessResponse(response)) {
            return Mono.just(getRateValue(response, convertTo));
        }
        final String errorMessage = "Error while fetching exchange rate for " + convertTo;
        log.error(errorMessage);
        return Mono.error(new IllegalArgumentException(errorMessage));
    }

    private double getRateValue(final JsonNode response, final String convertTo) {
        final JsonNode rate = response.get(ExchangeRateConstant.RATES_FIELD).get(convertTo);
        if(isNull(rate) || rate.isNull() || !rate.isNumber()) {
            throw new IllegalArgumentException("No rate found for " + convertTo);
        }
        return rate.asDouble();
    }

    private boolean isSuccessResponse(final JsonNode response) {
        final String result = response.path(ExchangeRateConstant.RESULT_FIELD).asText();
        return result == null || !result.equals(ExchangeRateConstant.ERROR_FIELD);
    }
}
