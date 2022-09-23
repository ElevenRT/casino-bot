package com.eleven.casinobot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * A class configurations the bot by referring to application.yml. <br>
 * get dangerous content if exposure from java code. <br>
 * for example: jdbc url, bot token
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private static final String TOKEN;
    private static final String DB_URL;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;
    private static final Boolean USE_DDL;

    private static final String ROOT_PACKAGE;

    /**
     * parse raw data from origin string
     * @param origin target to parse
     * @return raw data
     */
    private static String removeBrace(String origin) {
        return origin.replaceFirst("\\{", "")
                .replaceFirst("}", "");
    }

    static {
        try (final InputStream reader = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.yml")) {
            final Map<String, Object> data = new Yaml().load(reader);
            @SuppressWarnings("unchecked")
            Map<String, Object> properties = (Map<String, Object>) data.get("bot");
            @SuppressWarnings("unchecked")
            Map<String, Object> databaseProperties = (Map<String, Object>) properties.get("database");
            @SuppressWarnings("unchecked")
            Map<String, Object> rootPackageProperties = (Map<String, Object>) data.get("code");
            String token = removeBrace((String) properties.get("token"));
            if (isSystemProperty(token)) {
                TOKEN = getSystemProperty(token);
            }
            else {
                TOKEN = token;
            }
            log.debug("token: {}", TOKEN);

            String url = removeBrace((String) databaseProperties.get("url"));
            if (isSystemProperty(url)) {
                DB_URL = getSystemProperty(url);
            }
            else {
                DB_URL = url;
            }
            log.debug("database url: {}", DB_URL);

            String username = removeBrace((String) databaseProperties.get("username"));
            if (isSystemProperty(username)) {
                DB_USERNAME = getSystemProperty(username);
            }
            else {
                DB_USERNAME = username;
            }
            log.debug("username: {}", DB_USERNAME);

            String password = removeBrace((String) databaseProperties.get("password"));
            if (isSystemProperty(password)) {
                DB_PASSWORD = getSystemProperty(password);
            }
            else {
                DB_PASSWORD = password;
            }
            log.debug("password: {}", DB_PASSWORD);

            USE_DDL = Boolean.parseBoolean(databaseProperties.get("use-ddl").toString());
            log.debug("use ddl: {}", USE_DDL);

            String rootPackage = removeBrace((String) rootPackageProperties.get("package"));
            if (isSystemProperty(rootPackage)) {
                ROOT_PACKAGE = getSystemProperty(rootPackage);
            }
            else {
                ROOT_PACKAGE = rootPackage;
            }
            log.debug("root_package: {}", ROOT_PACKAGE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isSystemProperty(String propertyName) {
        return propertyName.startsWith("$");
    }

    private static String getSystemProperty(String propertyName) {
        return System.getenv(propertyName.replace("$", ""));
    }

    private AppConfig() {
        throw new RuntimeException();
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUsername() {
        return DB_USERNAME;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static boolean isUSE_DDL() {
        return USE_DDL;
    }

    public static String getRootPackage() {
        return ROOT_PACKAGE;
    }
}
