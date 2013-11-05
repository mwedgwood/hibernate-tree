package com.github.mwedgwood.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER =
            configureJsonObjectMapper(new ObjectMapper()).enable(SerializationFeature.INDENT_OUTPUT);

    public static String toJson(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> klass) throws IOException {
        return OBJECT_MAPPER.readValue(json, klass);
    }

    public static ObjectMapper configureJsonObjectMapper(ObjectMapper objectMapper) {
        // need to prevent hibernate lazy initialization errors during serialization
        objectMapper.registerModule(new Hibernate4Module());

        // Allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Write java.util.Date, Calendar as number (timestamp)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        // Prevent exception when encountering unknown property
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Coerce JSON empty String ("") to null
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        // Coerce unknown enum to null
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        // Force escaping of non-ASCII characters
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

        return objectMapper;
    }

}
