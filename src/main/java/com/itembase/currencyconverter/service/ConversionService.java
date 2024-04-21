package com.itembase.currencyconverter.service;

import com.itembase.currencyconverter.client.ExchangeRateProvider;
import com.itembase.currencyconverter.domain.dto.ConversionRequest;
import com.itembase.currencyconverter.domain.dto.ConversionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionService {
    private final List<ExchangeRateProvider> exchangeRateProviders;
    private final Random random = new Random();

    public Mono<ConversionResponse> convert(final ConversionRequest conversionRequest) {
        log.debug("Convert {} amount from {} to {}", conversionRequest.getAmount(), conversionRequest.getFrom(),
                conversionRequest.getTo());

        Collections.shuffle(exchangeRateProviders, random);
        return convertAmountByRandomProvider(exchangeRateProviders, conversionRequest, 0);
    }

    private Mono<ConversionResponse> convertAmountByRandomProvider(final List<ExchangeRateProvider> providers,
                                                                  final ConversionRequest conversionRequest,
                                                                   final int providerIndex) {
        if (providerIndex >= providers.size()) {
            return Mono.error(new IllegalStateException("No exchange rate provider available"));
        }

        final ExchangeRateProvider provider = providers.get(providerIndex);
        log.debug("Convert amount using provider {}", provider.getClass().getSimpleName());
        return provider.getExchangeRate(conversionRequest.getFrom(), conversionRequest.getTo())
                .flatMap(rate -> calculate(conversionRequest, rate))
                .onErrorResume(e -> convertAmountByRandomProvider(providers, conversionRequest, providerIndex + 1));
    }

    private Mono<ConversionResponse> calculate(final ConversionRequest conversionRequest, final double rate) {
        final double convertedAmount = conversionRequest.getAmount().doubleValue() * rate;
        return Mono.just(new ConversionResponse()
                .from(conversionRequest.getFrom())
                .to(conversionRequest.getTo())
                .amount(conversionRequest.getAmount())
                .converted(BigDecimal.valueOf(convertedAmount)));
    }
}