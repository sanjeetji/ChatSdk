package com.sanjeet.chat.sdk.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    // General key-value pairs
    private Map<String, Object> properties;
    private Map<String, String> value;

    // List of special configurations
    private List<String> specialKeys;

    // Nested complex settings
    private ComplexSettings complexSettings;

    // Getters and Setters

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, String> getValue() {
        return value;
    }

    public void setValue(Map<String, String> value) {
        this.value = value;
    }

    public List<String> getSpecialKeys() {
        return specialKeys;
    }

    public void setSpecialKeys(List<String> specialKeys) {
        this.specialKeys = specialKeys;
    }

    public ComplexSettings getComplexSettings() {
        return complexSettings;
    }

    public void setComplexSettings(ComplexSettings complexSettings) {
        this.complexSettings = complexSettings;
    }

    public static class ComplexSettings {
        private String url;
        private int timeout;
        private List<String> endpoints;

        // Getters and Setters

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public List<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<String> endpoints) {
            this.endpoints = endpoints;
        }
    }
}
