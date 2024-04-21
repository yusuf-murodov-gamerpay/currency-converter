package com.itembase.currencyconverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TestUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .registerModule(new JavaTimeModule());

    public static String readFileContentFromResource(String path) {
        try {
            ClassLoader classLoader = TestUtil.class.getClassLoader();
            URL url = classLoader.getResource(path);
            return IOUtils.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Exception during reading file content from resource", e);
            throw new IllegalStateException(e);
        }
    }
}
