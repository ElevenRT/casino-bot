package com.eleven.casinobot.database.container;

import com.eleven.casinobot.config.scanner.ReflectionScanner;
import com.eleven.casinobot.database.annotations.Database;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.database.DatabaseTemplate;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Look for existing database templates through reflection,
 * Class to manage database templates created
 * by a single tone pattern
 * @see DatabaseTemplate
 */
@SuppressWarnings("rawtypes")
public final class ContainerPool {

    private final Map<String, DatabaseTemplate> databaseTemplateContainer;

    private static ContainerPool containerPool;

    /**
     * Generates by reflection, stores real database template implementations
     * @return singleton container pool
     */
    public static ContainerPool getInstance() {
        if (containerPool == null) {
            try {
                containerPool = new ContainerPool();
            } catch (InvocationTargetException | NoSuchMethodException
                     | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return containerPool;
    }

    private ContainerPool() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        this.databaseTemplateContainer = new HashMap<>();
        saveTemplates();
    }

    private void saveTemplates() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        databaseTemplateContainer.putAll(findDataTemplates());
    }

    private Map<String, DatabaseTemplate> findDataTemplates()
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(
                AppConfig.getRootPackage(),
                new ReflectionScanner()
        );
        Set<Class<? extends DatabaseTemplate>> templates
                = reflections.getSubTypesOf(DatabaseTemplate.class);
        Map<String, DatabaseTemplate> databaseTemplates = new HashMap<>();

        for (Class<? extends DatabaseTemplate> clazz : templates) {
            if (clazz.isAnnotationPresent(Database.class)) {
                DatabaseTemplate template = clazz.getConstructor().newInstance();

                String simpleName = clazz.getSimpleName();
                if (databaseTemplates.get(simpleName) != null) {
                    throw new TemplateAlreadyExistsException();
                }

                databaseTemplates.put(simpleName, template);
            }
        }

        return databaseTemplates;
    }

    /**
     * returns singleton database templates
     * @return deep copy all singleton database templates
     * @throws CloneNotSupportedException if implement of
     * {@link DatabaseTemplate} class is not cloneable.
     */
    public Map<String, DatabaseTemplate> getDatabaseTemplateContainer()
            throws CloneNotSupportedException {
        Map<String, DatabaseTemplate> databaseTemplates = new HashMap<>();
        for (Map.Entry<String, DatabaseTemplate> templateEntry
                : databaseTemplateContainer.entrySet()) {
            databaseTemplates.put(templateEntry.getKey(), templateEntry.getValue().clone());
        }
        return databaseTemplates;
    }

    /**
     * Import a single-toned database template using the name of the database template.
     * @return single-toned database template
     */
    public DatabaseTemplate getDatabaseTemplate(String name) throws CloneNotSupportedException {
        DatabaseTemplate databaseTemplate = databaseTemplateContainer.get(name);
        if (databaseTemplate == null) {
            throw new TemplateNotDefinedException(name);
        }

        return databaseTemplate.clone();
    }
}
