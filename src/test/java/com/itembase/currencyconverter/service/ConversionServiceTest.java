package com.itembase.currencyconverter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itembase.currencyconverter.client.PrivateExchangeRateProvider;
import com.itembase.currencyconverter.client.PublicExchangeRateProvider;
import com.itembase.currencyconverter.domain.dto.ConversionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversionServiceTest {
    @Mock(lenient = true)
    private PrivateExchangeRateProvider privateExchangeRateProvider;
    @Mock(lenient = true)
    private PublicExchangeRateProvider publicExchangeRateProvider;
    private ConversionService conversionService;
    private ConversionRequest request;

    @BeforeEach
    void setup() {
        request = new ConversionRequest().from("EUR").to("USD").amount(BigDecimal.valueOf(15.50));
        conversionService = new ConversionService(Arrays.asList(privateExchangeRateProvider, publicExchangeRateProvider));
    }

    @Test
    void convert_shouldReturnConversionResponse_whenPrivateProviderReturnsResponse() {
        when(privateExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.just(0.85));
        when(publicExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.error(new IllegalArgumentException()));
        StepVerifier.create(conversionService.convert(request))
                .assertNext(response -> {
                    assertNotNull(response.getConverted());
                    assertEquals(request.getFrom(), response.getFrom());
                    assertEquals(request.getTo(), response.getTo());
                })
                .verifyComplete();

        verify(privateExchangeRateProvider, atMostOnce()).getExchangeRate(request.getFrom(), request.getTo());
    }

    @Test
    void convert_shouldReturnConversionResponse_whenPublicProviderReturnsResponse() {
        when(publicExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.just(0.85));
        when(privateExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(conversionService.convert(request))
                .assertNext(response -> {
                    assertNotNull(response.getConverted());
                    assertEquals(request.getFrom(), response.getFrom());
                    assertEquals(request.getTo(), response.getTo());
                })
                .verifyComplete();

        verify(publicExchangeRateProvider, atMostOnce()).getExchangeRate(request.getFrom(), request.getTo());
    }

    @Test
    void convert_shouldThrowException_WhenAllProvidersFailsToResponse() throws JsonProcessingException {
        when(publicExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.error(new IllegalArgumentException()));
        when(privateExchangeRateProvider.getExchangeRate(request.getFrom(), request.getTo()))
                .thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(conversionService.convert(request))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(publicExchangeRateProvider, atMostOnce()).getExchangeRate(request.getFrom(), request.getTo());
        verify(privateExchangeRateProvider, atMostOnce()).getExchangeRate(request.getFrom(), request.getTo());
    }
}
