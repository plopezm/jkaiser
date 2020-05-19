package com.aeox.jkaiser.utils;

import java.io.IOException;
import java.util.Map;

import javax.persistence.AttributeConverter;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
 
	private ObjectMapper objectMapper;
	
	@Autowired
    public HashMapConverter(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	@Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {
 
        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }
 
        return customerInfoJson;
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {
 
        Map<String, Object> customerInfo = null;
        try {
            customerInfo = objectMapper.readValue(customerInfoJSON, Map.class);
        } catch (final IOException e) {
        	log.error("JSON reading error", e);
        }
 
        return customerInfo;
    }
 
}