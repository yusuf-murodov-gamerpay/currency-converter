package com.itembase.currencyconverter.controller;

import com.itembase.currencyconverter.domain.dto.ConversionRequest;
import com.itembase.currencyconverter.domain.dto.ConversionResponse;
import com.itembase.currencyconverter.rest.resource.ConvertApi;
import com.itembase.currencyconverter.service.ConversionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Api(tags = "Currency converter")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConversionController implements ConvertApi {
    private final ConversionService conversionService;

    @Override
    public Mono<ResponseEntity<ConversionResponse>> convert(Mono<ConversionRequest> conversionRequest,
                                                            ServerWebExchange exchange) {
        log.info("Called convert");
        return conversionRequest
                .flatMap(conversionService::convert)
                .map(ResponseEntity::ok);
    }
}
