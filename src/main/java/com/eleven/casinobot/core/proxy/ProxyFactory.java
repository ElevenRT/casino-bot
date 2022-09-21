package com.eleven.casinobot.core.proxy;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public final class ProxyFactory {

    public static <T> T newProxy(Object origin, Class<T> interfaces) {
        return newProxy(origin, interfaces, before -> {}, after -> {});
    }

    public static <T> T newProxy(Object origin, Class<T> interfaces,
                                 Consumer<T> before) {
        return newProxy(origin, interfaces, before, after -> {});
    }

    public static <T> T newProxy(Object origin, Class<T> interfaces,
                                 Consumer<T> before, Consumer<T> after) {
        T t = interfaces.cast(origin);
        ProxyHandler<T> proxyHandler = proxyHandler(t, before, after);
        return createProxy(origin.getClass(), interfaces, proxyHandler);
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
