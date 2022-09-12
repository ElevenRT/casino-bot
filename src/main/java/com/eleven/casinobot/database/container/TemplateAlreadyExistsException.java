package com.eleven.casinobot.database.container;

/**
 * TemplateAlreadyExistsException is database template exception class
 * that can be thrown during create new database template.
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class TemplateAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1662884804804L;

    public TemplateAlreadyExistsException() {
        super("A database template with the same name already exists.");
    }
}
