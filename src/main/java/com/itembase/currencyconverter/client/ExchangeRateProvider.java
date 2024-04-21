package com.itembase.currencyconverter.client;

import reactor.core.publisher.Mono;

public interface ExchangeRateProvider {
    Mono<Double> getExchangeRate(final String base, final String convertTo);
}
