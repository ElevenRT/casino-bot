package com.eleven.casinobot.database.proxy;

/**
 * ProxyAlreadyExistsException is proxy exception class
 * that can be thrown during create new proxy instance.
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
public class ProxyAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1662884804804L;

    public ProxyAlreadyExistsException() {
        super("A proxy database template with the same name already exists.");
    }
}
