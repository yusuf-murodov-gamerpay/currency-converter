package com.itembase.currencyconverter.service;

import com.itembase.currencyconverter.client.ExchangeRateProvider;
import com.itembase.currencyconverter.domain.dto.ConversionRequest;
import com.itembase.currencyconverter.domain.dto.ConversionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        final ExchangeRateProvider provider = providers.get(providerIndex);
        log.debug("Convert amount using provider {}", provider.getClass().getSimpleName());
        return provider.getExchangeRate(conversionRequest.getFrom(), conversionRequest.getTo())
                .flatMap(rate -> calculate(conversionRequest, rate))
                .onErrorResume(e -> handleErrorWithFallback(providers, conversionRequest, providerIndex + 1, e));
    }

    private Mono<ConversionResponse> handleErrorWithFallback(final List<ExchangeRateProvider> providers,
                                                             final ConversionRequest conversionRequest, final int index,
                                                             final Throwable e) {
        if(index >= providers.size()) {
            log.error("Error while converting amount", e);
            return Mono.error(e);
        }
        return convertAmountByRandomProvider(providers, conversionRequest, index);
    }

    private Mono<ConversionResponse> calculate(final ConversionRequest conversionRequest, final double rate) {
        final BigDecimal convertedAmount = conversionRequest.getAmount()
                .multiply(BigDecimal.valueOf(rate))
                .setScale(2, RoundingMode.HALF_UP);

        return Mono.just(new ConversionResponse()
                .from(conversionRequest.getFrom())
                .to(conversionRequest.getTo())
                .amount(conversionRequest.getAmount())
                .converted(convertedAmount));
    }
}
