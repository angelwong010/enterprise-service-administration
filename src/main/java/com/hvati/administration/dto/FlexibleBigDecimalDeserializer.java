package com.hvati.administration.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Deserializes JSON number or string to BigDecimal.
 * Accepts integers, decimals, and string numbers so PATCH from frontend (JavaScript numbers) always works.
 */
public class FlexibleBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentToken()) {
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                try {
                    return p.getDecimalValue();
                } catch (Exception e) {
                    return BigDecimal.valueOf(p.getDoubleValue());
                }
            case VALUE_STRING:
                String s = p.getText();
                if (s == null || s.isBlank()) return null;
                return new BigDecimal(s.trim());
            case VALUE_NULL:
                return null;
            default:
                return (BigDecimal) ctxt.handleUnexpectedToken(BigDecimal.class, p);
        }
    }
}
