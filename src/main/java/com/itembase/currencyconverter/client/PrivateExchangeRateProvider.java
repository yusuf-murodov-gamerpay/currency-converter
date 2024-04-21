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
public class PrivateExchangeRateProvider extends ExchangeRateProvider {

    public PrivateExchangeRateProvider(WebClient privateExchangeRateProviderWebClient) {
        super(privateExchangeRateProviderWebClient);
    }

    @Override
    protected URI getExchangeRateUri(final UriBuilder uriBuilder, final String base, final String convertTo) {
        return uriBuilder.queryParam("base", base).queryParam("symbols", convertTo).build();
    }

    @Override
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
        return response.path(ExchangeRateConstant.SUCCESS_FIELD).asBoolean();
    }
}
