package com.eleven.casinobot.database.container;

/**
 * TemplateNotDefinedException is database template exception class
 * that can be thrown during get single-toned template.
 */
public final class TemplateNotDefinedException extends RuntimeException {

    private static final long serialVersionUID = 1662889609608L;

    public TemplateNotDefinedException(String name) {
        super(String.format("'%s' is undefined database template value.", name));
    }

    public <T> TemplateNotDefinedException(String name, T type) {
        super(String.format("'%s' is undefined database template value. " +
                "'%s' type is non-database-template ", name, type));
    }
}
