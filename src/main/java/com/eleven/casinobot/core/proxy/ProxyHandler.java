package com.eleven.casinobot.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ProxyHandler<T> implements InvocationHandler {

    private final T origin;
    private final Consumer<T> before;
    private final Consumer<T> after;

    public ProxyHandler(T origin, Consumer<T> before, Consumer<T> after) {
        this.origin = origin;
        this.before = before;
        this.after = after;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object result = method.invoke(origin, args);
        before.andThen((proxyMethod) -> {
            try {
                method.invoke(proxyMethod, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).andThen(after).accept(origin);
        return result;
    }
}
