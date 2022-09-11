package com.eleven.casinobot.database.proxy;

import com.eleven.casinobot.annotations.Database;
import com.eleven.casinobot.database.DatabaseTemplate;
import javassist.Modifier;
import javassist.util.proxy.ProxyFactory;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Look for existing database templates through reflection,
 * Class to manage proxy database templates created by proxy in a single tone pattern
 * @see DatabaseTemplate
 */
@SuppressWarnings("rawtypes")
public class ProxyPool {

    private final Map<String, DatabaseTemplate> proxyDatabaseTemplates;

    private static ProxyPool proxyPool;

    public static ProxyPool getInstance() {
        if (proxyPool == null) {
            try {
                proxyPool = new ProxyPool();
            } catch (InvocationTargetException | NoSuchMethodException
                     | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return proxyPool;
    }

    // reflection 을 발생시켜, 진짜 데이터베이스 템플릿 구현체들을 저장해야함
    private ProxyPool() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        this.proxyDatabaseTemplates = new HashMap<>();
        saveProxyTemplates();
    }

    private void saveProxyTemplates() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        proxyDatabaseTemplates.putAll(findDataTemplates());
    }

    private Map<String, DatabaseTemplate> findDataTemplates()
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.eleven.casinobot");
        Set<Class<?>> templates = reflections.getTypesAnnotatedWith(Database.class);
        Map<String, DatabaseTemplate> databaseTemplates = new HashMap<>();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(DatabaseTemplate.class);
        proxyFactory.setFilter(
                method -> {
                    int modifier = method.getModifiers();
                    return (Modifier.isAbstract(modifier) && Modifier.isProtected(modifier));
                }
        );

        for (Class<?> clazz : templates) {
            if (clazz.getSuperclass() == DatabaseTemplate.class) {
                DatabaseTemplate template = (DatabaseTemplate) proxyFactory.create(
                        new Class[]{}, new Object[]{},
                        new ProxyHandler((DatabaseTemplate) clazz.getConstructor().newInstance())
                );
                String simpleName = clazz.getSimpleName();
                if (databaseTemplates.get(simpleName) != null) {
                    throw new ProxyAlreadyExistsException();
                }

                databaseTemplates.put(simpleName, template);
            }
        }

        return databaseTemplates;
    }

    /**
     * returns proxy database templates
     * @return deep copy all proxy database templates
     * @throws CloneNotSupportedException if implement of {@link DatabaseTemplate} class is not cloneable.
     */
    public Map<String, DatabaseTemplate> getProxyDatabaseTemplates()
            throws CloneNotSupportedException {
        Map<String, DatabaseTemplate> databaseTemplates = new HashMap<>();
        for (Map.Entry<String, DatabaseTemplate> templateEntry
                : proxyDatabaseTemplates.entrySet()) {
            databaseTemplates.put(templateEntry.getKey(), templateEntry.getValue().clone());
        }
        return databaseTemplates;
    }
}
