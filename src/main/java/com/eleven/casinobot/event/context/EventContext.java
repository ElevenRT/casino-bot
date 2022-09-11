package com.eleven.casinobot.event.context;

import com.eleven.casinobot.config.scanner.ReflectionScanner;
import com.eleven.casinobot.event.annotations.EventListener;
import com.eleven.casinobot.event.annotations.Inject;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.database.DatabaseTemplate;
import com.eleven.casinobot.database.container.TemplateNotDefinedException;
import com.eleven.casinobot.database.container.ContainerPool;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Inject 어노테이션이 있는 필드에 직접 주입을 합니다.
 * 해당 타입이 DatabaseTemplate 이고, 필드명이 프록시로 저장되어 있다면,
 * 주입해주고, 그렇지않다면, 예외를 발생합니다.
 * @see TemplateNotDefinedException
 */
public final class EventContext {
    private static final Map<Class<?>, Object> contextRegistry = new HashMap<>();

    private final ContainerPool PROXY_POOL;

    public EventContext() {
        PROXY_POOL = ContainerPool.getInstance();
        initializeContext();
    }

    private void initializeContext() {
        Set<Class<?>> classes = findClasses(AppConfig.getRootPackage());
        for (Class<?> loadingClass : classes) {
            try {
                if (loadingClass.isAnnotationPresent(EventListener.class)) {
                    Constructor<?> constructor = loadingClass.getDeclaredConstructor();
                    Object instance = constructor.newInstance();
                    contextRegistry.put(loadingClass, instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Set<Class<?>> findClasses(String packageName) {
        Reflections reflections = new Reflections(
                packageName, new ReflectionScanner()
        );
        return new HashSet<>(reflections.getSubTypesOf(Object.class));
    }

    public <T> T getInstance(Class<T> clazz) throws IllegalAccessException {
        @SuppressWarnings("unchecked")
        T object = (T) contextRegistry.get(clazz);
        if (object == null) {
            throw new TemplateNotDefinedException(clazz.getSimpleName());
        }

        Field[] declaredField = clazz.getDeclaredFields();
        injectAnnotatedField(object, declaredField);
        return object;
    }

    private <T> void injectAnnotatedField(T object, Field[] declaredFields) throws IllegalAccessException {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Inject.class)) {
                final Class<?> type = field.getType();
                final String name = (field.getAnnotation(Inject.class).name().equals(""))
                        ? type.getSimpleName() : field.getAnnotation(Inject.class).name();
                if (type.getSuperclass() != DatabaseTemplate.class && type != DatabaseTemplate.class) {
                    throw new TemplateNotDefinedException(name, type);
                }

                int modifier = field.getModifiers();
                if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier)) {
                    field.setAccessible(true);
                }

                field.set(object, getProxyDatabase(name));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private DatabaseTemplate getProxyDatabase(String name) {
        try {
            return PROXY_POOL.getDatabaseTemplate(name);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
