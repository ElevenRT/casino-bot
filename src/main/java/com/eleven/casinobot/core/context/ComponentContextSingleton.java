package com.eleven.casinobot.core.context;

/**
 * A class that stores event contexts in a single tone manner, simply in a single tone.
 */
public final class ComponentContextSingleton {

    private static ComponentContext componentContext;

    public static ComponentContext getInstance() {
        return getInstance(false);
    }

    public static ComponentContext getInstance(boolean includeDeprecated) {
        if (componentContext == null) {
            componentContext = new ComponentContext(includeDeprecated);
        }

        return componentContext;
    }
}
