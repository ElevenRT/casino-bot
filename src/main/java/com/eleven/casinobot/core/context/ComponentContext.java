package com.eleven.casinobot.core.context;

import com.eleven.casinobot.core.annotations.*;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.core.interaction.component.button.IButtonInteraction;
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

    public Map<Class<?>, ?> getAllCommands(Class<?>... interfaces) throws IllegalAccessException {
        return getContext(Command.class, new HashSet<>(), interfaces);
    }

    public Map<String, String> getAllInteractionInfos() throws IllegalAccessException {
        Map<Class<?>, ?> commands = getAllCommands();
        Map<String, String> interactions = new HashMap<>();
        for (Map.Entry<Class<?>, ?> command : commands.entrySet()) {
            Command cmd = command.getKey().getAnnotation(Command.class);
            CommandDetail detail = cmd.detail();
            if ((detail.commandType() == CommandDetail.Type.COMMAND)
                    || (interactions.containsKey(detail.gameType()))
                    || (detail.interactionId().equals(""))) {
                continue;
            }

            interactions.put(detail.gameType(), detail.interactionId());
        }
        return interactions;
    }
    
    public Map<Class<?>, Object> getAllInteractions() throws IllegalAccessException {
        Map<Class<?>, ?> commands = getAllCommands(IButtonInteraction.class);
        Map<Class<?>, Object> interactions = new HashMap<>();
        for (Map.Entry<Class<?>, ?> command : commands.entrySet()) {
            Command cmd = command.getKey().getAnnotation(Command.class);
            CommandDetail detail = cmd.detail();
            if ((detail.commandType() == CommandDetail.Type.COMMAND)
                    || (detail.interactionId().equals(""))) {
                continue;
            }

            interactions.put(command.getKey(), command.getValue());
        }
        return interactions;
    }

    private Map<Class<?>, Object> getContext(Class<? extends Annotation> annotation,
                              Collection<Object> instances, Class<?>... interfaces) throws IllegalAccessException {
        Map<Class<?>, Object> map = new HashMap<>();
        for (Map.Entry<Class<?>, Object> listener : contextRegistry.entrySet()) {

            Class<?> clazz = listener.getKey();
            if (!clazz.isAnnotationPresent(annotation)) {
                continue;
            }

            if (interfaces.length != 0 && clazz.getInterfaces().length > 1) {
                int length = interfaces.length;
                Class<?>[] classes = clazz.getInterfaces();

                int interfaceCount = 0;
                for (int l = 0; l < length; l++) {
                    for (Class<?> c : classes) {
                        for (Class<?> i : interfaces) {
                            if (c.equals(i)) {
                                interfaceCount++;
                                break;
                            }
                        }
                    }
                }

                if (interfaceCount < length) {
                    throw new IllegalArgumentException(String.format("expected interface count: %d, but actually: %d. " +
                            "exception has happen at %s class", length, interfaceCount, clazz.getSimpleName()));
                }
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
