package com.eleven.casinobot.core.context;

import com.eleven.casinobot.core.annotations.*;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.core.util.Utils;
import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import com.eleven.casinobot.database.container.TemplateNotDefinedException;

import java.lang.annotation.Annotation;
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
 * @see AbstractDatabaseTemplate
 * @author iqpizza6349
 * @version 1.0.0
 */
public final class ComponentContext {
    private static final Map<Class<?>, Object> contextRegistry = new HashMap<>();

    public ComponentContext(boolean includeDeprecated) {
        initializeContext(includeDeprecated);
    }

    private void initializeContext(boolean includeDeprecated) {
        Set<Class<?>> classes = Utils.findClasses(
                AppConfig.getRootPackage(),
                Component.class
        );
        Set<String> commandValues = new HashSet<>();
        for (Class<?> loadingClass : classes) {
            try {
                if (loadingClass.isAnnotation()) {
                    continue;
                }

                if (!includeDeprecated && loadingClass.isAnnotationPresent(Deprecated.class)) {
                    continue;
                }

                if (loadingClass.isAnnotationPresent(Command.class)) {
                    Command command = loadingClass.getAnnotation(Command.class);
                    if (commandValues.contains(command.value())) {
                        throw new RuntimeException("A command with duplicate values exists.");
                    }
                    commandValues.add(command.value());
                }

                Class<?>[] fields = Utils.findConstructorFields(loadingClass);
                Constructor<?> constructor = loadingClass.getConstructor(fields);

                Object instance = constructor.newInstance(Utils.dependencyDatabase(fields));
                contextRegistry.put(loadingClass, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public Set<Object> getAllEventHandler() throws IllegalAccessException {
        Set<Object> instances = new HashSet<>();
        getContext(EventHandler.class, instances);
        return instances;
    }

    public Map<Class<?>, ?> getAllCommands() throws IllegalAccessException {
        return getContext(Command.class, new HashSet<>());
    }

    public Map<String, Set<String>> getAllGameInfos() throws IllegalAccessException {
        Map<Class<?>, ?> commands = getAllCommands();
        Map<String, Set<String>> games = new HashMap<>();
        for (Map.Entry<Class<?>, ?> command : commands.entrySet()) {
            Command cmd = command.getKey().getAnnotation(Command.class);
            CommandDetail detail = cmd.detail();
            if ((detail.commandType() == CommandDetail.Type.COMMAND)
                    || (games.containsKey(detail.gameType()))
                    || (detail.interactionIds().length == 0)) {
                continue;
            }

            games.put(detail.gameType(), Set.of(detail.interactionIds()));
        }
        return games;
    }

    private Map<Class<?>, Object> getContext(Class<? extends Annotation> annotation,
                              Collection<Object> instances) throws IllegalAccessException {
        Map<Class<?>, Object> map = new HashMap<>();
        for (Map.Entry<Class<?>, Object> listener : contextRegistry.entrySet()) {

            if (!listener.getKey().isAnnotationPresent(annotation)) {
                continue;
            }

            Object instance = listener.getValue();
            instances.add(instance);
            Field[] declaredField = listener.getKey().getDeclaredFields();
            injectAnnotatedField(instance, declaredField);
            map.put(listener.getKey(), listener.getValue());
        }

        return map;
    }



    @SuppressWarnings("deprecation")
    private <T> void injectAnnotatedField(T object, Field[] declaredFields) throws IllegalAccessException {
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Injection.class)) {
                final Class<?> type = field.getType();
                final String name = (field.getAnnotation(Injection.class).name().equals(""))
                        ? type.getSimpleName() : field.getAnnotation(Injection.class).name();
                Utils.checkDatabaseField(type, name);

                int modifier = field.getModifiers();
                if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier)) {
                    field.setAccessible(true);
                }

                field.set(object, Utils.getSingletonDatabase(name));
            }
        }
    }
}
