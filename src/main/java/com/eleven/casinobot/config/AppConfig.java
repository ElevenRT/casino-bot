package com.eleven.casinobot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * A class configurations the bot by referring to application.yml. <br>
 * get danagerous content if exposure from java code. <br>
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
            TOKEN = removeBrace((String) properties.get("token"));
            log.debug("token: {}", TOKEN);
            DB_URL = removeBrace((String) databaseProperties.get("url"));
            log.debug("database url: {}", DB_URL);
            DB_USERNAME = removeBrace((String) databaseProperties.get("username"));
            log.debug("username: {}", DB_USERNAME);
            DB_PASSWORD = removeBrace((String) databaseProperties.get("password"));
            log.debug("password: {}", DB_PASSWORD);
            USE_DDL = Boolean.parseBoolean(databaseProperties.get("use-ddl").toString());
            log.debug("use ddl: {}", USE_DDL);
            ROOT_PACKAGE = removeBrace((String) rootPackageProperties.get("package"));
            log.debug("root_package: {}", ROOT_PACKAGE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
