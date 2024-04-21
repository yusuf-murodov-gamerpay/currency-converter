package com.itembase.currencyconverter.configuration;

import com.itembase.currencyconverter.configuration.properties.ExchangeClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ExchangeClientProperties.class)
public class ClientConfiguration {

    private HttpClient httpClient(ExchangeClientProperties exchangeClientProperties) throws SSLException {
        final ExchangeClientProperties.Properties properties = exchangeClientProperties.getProperties();
        final SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        return HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectionTimeout())
                .responseTimeout(Duration.ofMillis(properties.getResponseTimeout()))
                .doOnConnected(connection -> connection
                        .addHandlerFirst(new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(properties.getWriteTimeout(), TimeUnit.MILLISECONDS)));
    }

    @Bean
    public WebClient publicExchangeRateProviderWebClient(ExchangeClientProperties exchangeClientProperties)
            throws SSLException {

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient(exchangeClientProperties)))
                .baseUrl(exchangeClientProperties.getPublicProvider().getUrl())
                .build();
    }

    @Bean
    public WebClient privateExchangeRateProviderWebClient(ExchangeClientProperties exchangeClientProperties)
            throws SSLException {

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient(exchangeClientProperties)))
                .baseUrl(exchangeClientProperties.getPrivateProvider().getUrl())
                .filter(addQueryParam("access_key", exchangeClientProperties.getPrivateProvider().getApiKey()))
                .build();
    }

    private ExchangeFilterFunction addQueryParam(String name, String value) {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> Mono.just(ClientRequest.from(clientRequest)
                .url(UriComponentsBuilder.fromUri(clientRequest.url()).queryParam(name, value).build().toUri())
                .build()));
    }
}
