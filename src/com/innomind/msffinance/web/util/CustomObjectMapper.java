package com.innomind.msffinance.web.util;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
public class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        SimpleModule module = new SimpleModule("JsonDateModule", 
        		new Version(2, 0, 0, null, null, null));
        module.addSerializer(Date.class, new AppDateSerializer());
        module.addDeserializer(Date.class, new AppDateDeSerializer());
        registerModule(module);
  }
}