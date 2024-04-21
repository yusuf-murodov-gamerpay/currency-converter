package com.itembase.currencyconverter.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ExchangeClientProperties.PREFIX)
@Getter
@Setter
public class ExchangeClientProperties {
    public static final String PREFIX = "application.exchange.client";

    private Properties properties;
    private Provider privateProvider;
    private Provider publicProvider;

    @Getter
    @Setter
    public static class Properties {
        private int connectionTimeout;
        private int readTimeout;
        private int responseTimeout;
        private int writeTimeout;

    }

    @Getter
    @Setter
    public static class Provider {
        private String url;
        private String apiKey;
    }
}
