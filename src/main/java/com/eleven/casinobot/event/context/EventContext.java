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
import java.util.*;

/**
 * Collecting Classes and make instances which has EventHandler annotation.
 * Using Injection annotation Inject directly into the field.
 * If this field type is Database Template, inject the data(class) in field.
 * otherwise, an exception occurs
 *
 * @see TemplateNotDefinedException
 * @see EventHandler
 * @see Injection
 * @see DatabaseTemplate
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
                    if (loadingClass.isAnnotationPresent(Deprecated.class)
                            && !loadingClass.isAnnotation()) {
                        continue;
                    }
                }

                Class<?>[] fields = findConstructorFields(loadingClass);
                Constructor<?> constructor = loadingClass.getConstructor(fields);

                Object instance = constructor.newInstance(dependencyDatabase(fields));
                contextRegistry.put(loadingClass, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Class<?>[] findConstructorFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        final int fieldCount = fields.length;
        Class<?>[] types = new Class<?>[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            Field field = fields[i];
            int modifier = field.getModifiers();
            if (Modifier.isStatic(modifier)) {
                continue;
            }

            if ((!Modifier.isPrivate(modifier)) || (!Modifier.isFinal(modifier))) {
                continue;
            }

            checkDatabaseField(field.getType(), field.getName());
            types[i] = field.getType();
        }

        return Arrays.stream(types)
                .filter(Objects::nonNull)
                .toArray(Class[]::new);
    }


    private Set<Class<?>> findClasses(String packageName) {
        Reflections reflections = new Reflections(
                packageName, Scanners.TypesAnnotated
        );
        return new HashSet<>(reflections.getTypesAnnotatedWith(EventHandler.class));
    }

    private Object[] dependencyDatabase(Class<?>[] dependencyFields) {
        final int fieldCount = dependencyFields.length;
        Object[] dependencies = new Object[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            Class<?> clazz = dependencyFields[i];
            dependencies[i] = getSingletonDatabase(clazz.getSimpleName());
        }

        return dependencies;
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

    @SuppressWarnings("deprecation")
    private <T> void injectAnnotatedField(T object, Field[] declaredFields) throws IllegalAccessException {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Injection.class)) {
                final Class<?> type = field.getType();
                final String name = (field.getAnnotation(Injection.class).name().equals(""))
                        ? type.getSimpleName() : field.getAnnotation(Injection.class).name();
                checkDatabaseField(type, name);

                int modifier = field.getModifiers();
                if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier)) {
                    field.setAccessible(true);
                }

                field.set(object, getSingletonDatabase(name));
            }
        }
    }

    private void checkDatabaseField(Class<?> type, String name) {
        if (type.getSuperclass() != DatabaseTemplate.class && type != DatabaseTemplate.class) {
            throw new TemplateNotDefinedException(name, type);
        }
    }

    @SuppressWarnings("rawtypes")
    private DatabaseTemplate getSingletonDatabase(String name) {
        try {
            return containerPool.getDatabaseTemplate(name);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
