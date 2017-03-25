package com.innomind.msffinance.web.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class AppDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator jgen,
        SerializerProvider provider) throws IOException,JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        jgen.writeString(format.format(value));
    }

    @Override
    public Class<Date> handledType() {
        return Date.class;
    }
}