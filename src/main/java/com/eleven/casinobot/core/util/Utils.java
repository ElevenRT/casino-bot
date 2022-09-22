package com.eleven.casinobot.core.util;

import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import com.eleven.casinobot.database.container.ContainerPool;
import com.eleven.casinobot.database.container.TemplateNotDefinedException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Utils {

    private Utils() {
        throw new RuntimeException();
    }

    private static final ContainerPool containerPool = ContainerPool.getInstance();

    public static Class<?>[] findConstructorFields(Class<?> clazz) {
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

    public static void checkDatabaseField(Class<?> type, String name) {
        if (type.getSuperclass() != AbstractDatabaseTemplate.class && type != AbstractDatabaseTemplate.class) {
            throw new TemplateNotDefinedException(name, type);
        }
    }

    public static Object[] dependencyDatabase(Class<?>[] dependencyFields) {
        final int fieldCount = dependencyFields.length;
        Object[] dependencies = new Object[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            Class<?> clazz = dependencyFields[i];
            dependencies[i] = getSingletonDatabase(clazz.getSimpleName());
        }

        return dependencies;
    }

    @SuppressWarnings("rawtypes")
    public static AbstractDatabaseTemplate getSingletonDatabase(String name) {
        try {
            return containerPool.getDatabaseTemplate(name);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> findClasses(String packageName,
                                            Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(
                packageName, Scanners.TypesAnnotated
        );
        return new HashSet<>(reflections.getTypesAnnotatedWith(annotation));
    }
}
