package com.eleven.casinobot.database.proxy;

import com.eleven.casinobot.database.DatabaseTemplate;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

/**
 * Class that handles the methods accessed by the proxy.
 * The class is designed to handle only the protected abstract method.
 * @see MethodHandler
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
public class ProxyHandler implements MethodHandler {

    private final DatabaseTemplate databaseTemplate;

    public ProxyHandler(DatabaseTemplate databaseTemplate) {
        this.databaseTemplate = databaseTemplate;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
            throws Throwable {
        thisMethod.setAccessible(true);
        return thisMethod.invoke(databaseTemplate, args);
    }
}
