package com.eleven.casinobot.context.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public class ProxyFactory {

    public static <T> T newProxy(Class<? extends T> origin, Class<T> interfaces) {
        return newProxy(origin, interfaces, before -> {}, after -> {});
    }

    public static <T> T newProxy(Class<? extends T> origin, Class<T> interfaces,
                                 Consumer<T> before) {
        return newProxy(origin, interfaces, before, after -> {});
    }

    public static <T> T newProxy(Class<? extends T> origin, Class<T> interfaces,
                                 Consumer<T> before, Consumer<T> after) {
        T t;
        try {
             t = origin.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        ProxyHandler<T> proxyHandler = proxyHandler(t, before, after);
        return createProxy(origin, interfaces, proxyHandler);
    }

    private ProxyFactory() {
        throw new RuntimeException();
    }

    private static <T> ProxyHandler<T> proxyHandler(T origin,
                                                 Consumer<T> before, Consumer<T> after) {
        return new ProxyHandler<>(origin, before, after);
    }

    private static <T> T createProxy(Class<?> origin, Class<T> interfaces,
                                     ProxyHandler<T> proxyHandler) {
        return interfaces.cast(Proxy.newProxyInstance(origin.getClassLoader(),
                new Class[]{interfaces}, proxyHandler));
    }
}
