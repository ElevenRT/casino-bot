package com.eleven.casinobot.event.context;

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
