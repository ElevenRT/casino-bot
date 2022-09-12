package com.eleven.casinobot.event.context;

import com.eleven.casinobot.event.annotations.EventHandler;
import com.eleven.casinobot.event.annotations.Injection;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.database.DatabaseTemplate;
import com.eleven.casinobot.database.container.TemplateNotDefinedException;
import com.eleven.casinobot.database.container.ContainerPool;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Inject directly into the field with the Inject annotation.
 * If this type is Database Template and you have a database template with a field name, inject the data in field
 * otherwise, an exception occurs.
 * @see TemplateNotDefinedException
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class EventContext {
    private static final Map<Class<?>, Object> contextRegistry = new HashMap<>();

    private final ContainerPool containerPool;

    public EventContext(boolean includeDeprecated) {
        containerPool = ContainerPool.getInstance();
        initializeContext(includeDeprecated);
    }

    private void initializeContext(boolean includeDeprecated) {
        Set<Class<?>> classes = findClasses(AppConfig.getRootPackage());
        for (Class<?> loadingClass : classes) {
            try {
                if (!includeDeprecated) {
                    if (loadingClass.isAnnotationPresent(Deprecated.class)) {
                        continue;
                    }
                }

                Constructor<?> constructor = loadingClass.getDeclaredConstructor();
                Object instance = constructor.newInstance();
                contextRegistry.put(loadingClass, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Set<Class<?>> findClasses(String packageName) {
        Reflections reflections = new Reflections(
                packageName, Scanners.TypesAnnotated
        );
        return new HashSet<>(reflections.getTypesAnnotatedWith(EventHandler.class));
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

    public Set<?> getAllEventHandler() throws IllegalAccessException {
        Set<Object> eventHandler = new HashSet<>();
        for (Map.Entry<Class<?>, Object> listener : contextRegistry.entrySet()) {
            Object instance = listener.getValue();
            eventHandler.add(instance);
            Field[] declaredField = listener.getKey().getDeclaredFields();
            injectAnnotatedField(instance, declaredField);
        }

        return eventHandler;
    }

    private <T> void injectAnnotatedField(T object, Field[] declaredFields) throws IllegalAccessException {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Injection.class)) {
                final Class<?> type = field.getType();
                final String name = (field.getAnnotation(Injection.class).name().equals(""))
                        ? type.getSimpleName() : field.getAnnotation(Injection.class).name();
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
            return containerPool.getDatabaseTemplate(name);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
