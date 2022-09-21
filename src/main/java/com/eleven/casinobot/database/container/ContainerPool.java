package com.eleven.casinobot.database.container;

import com.eleven.casinobot.config.scanner.ReflectionScanner;
import com.eleven.casinobot.database.annotations.Database;
import com.eleven.casinobot.config.AppConfig;
import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Look for existing database templates through reflection,
 * Class to manage database templates created
 * by a single tone pattern
 * @see AbstractDatabaseTemplate
 */
@SuppressWarnings("rawtypes")
public final class ContainerPool {

    private final Map<String, AbstractDatabaseTemplate> databaseTemplateContainer;

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

    private Map<String, AbstractDatabaseTemplate> findDataTemplates()
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(
                AppConfig.getRootPackage(),
                new ReflectionScanner()
        );
        Set<Class<? extends AbstractDatabaseTemplate>> templates
                = reflections.getSubTypesOf(AbstractDatabaseTemplate.class);
        Map<String, AbstractDatabaseTemplate> databaseTemplates = new HashMap<>();

        for (Class<? extends AbstractDatabaseTemplate> clazz : templates) {
            if (clazz.isAnnotationPresent(Database.class)) {
                AbstractDatabaseTemplate template = clazz.getConstructor().newInstance();

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
     * {@link AbstractDatabaseTemplate} class is not cloneable.
     */
    public Map<String, AbstractDatabaseTemplate> getDatabaseTemplateContainer()
            throws CloneNotSupportedException {
        Map<String, AbstractDatabaseTemplate> databaseTemplates = new HashMap<>();
        for (Map.Entry<String, AbstractDatabaseTemplate> templateEntry
                : databaseTemplateContainer.entrySet()) {
            databaseTemplates.put(templateEntry.getKey(), templateEntry.getValue().clone());
        }
        return databaseTemplates;
    }

    /**
     * Import a single-toned database template using the value of the database template.
     * @return single-toned database template
     */
    public AbstractDatabaseTemplate getDatabaseTemplate(String name) throws CloneNotSupportedException {
        AbstractDatabaseTemplate abstractDatabaseTemplate = databaseTemplateContainer.get(name);
        if (abstractDatabaseTemplate == null) {
            throw new TemplateNotDefinedException(name);
        }

        return abstractDatabaseTemplate.clone();
    }
}
