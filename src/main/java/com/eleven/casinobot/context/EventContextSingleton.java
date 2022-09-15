package com.eleven.casinobot.context;

/**
 * A class that stores event contexts in a single tone manner, simply in a single tone.
 */
public final class EventContextSingleton {

    private static EventContext eventContext;

    public static EventContext getInstance() {
        return getInstance(false);
    }

    public static EventContext getInstance(boolean includeDeprecated) {
        if (eventContext == null) {
            eventContext = new EventContext(includeDeprecated);
        }

        return eventContext;
    }
}
