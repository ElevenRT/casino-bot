package com.eleven.casinobot.config;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public final class AppConfig {

    private static final String TOKEN;

    private static String removeBrace(String origin) {
        return origin.replaceFirst("\\{", "")
                .replaceFirst("}", "");
    }

    static {
        try (InputStream reader = AppConfig.class.getClassLoader().getResourceAsStream("application.yml")) {
            final Map<String, Object> data = new Yaml().load(reader);
            @SuppressWarnings("unchecked")
            Map<String, Object> properties = (Map<String, Object>) data.get("bot");
            TOKEN = removeBrace((String) properties.get("token"));
            System.out.println(TOKEN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AppConfig() {
        throw new RuntimeException();
    }

    public static String getToken() {
        return TOKEN;
    }
}
