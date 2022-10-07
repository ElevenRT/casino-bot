package com.eleven.casinobot.core.interaction.component.menu;

import com.eleven.casinobot.core.context.ComponentContextSingleton;

import java.util.HashMap;
import java.util.Map;

public final class MenuInteractionManager {

    private static final Map<Class<?>, Object> MENUS = new HashMap<>();

    static {
        try {
            MENUS.putAll(ComponentContextSingleton.getInstance().getAllCommands(IMenuInteraction.class));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println(MENUS);
    }

    public static void init() {

    }

    public static void invokeInteraction(IMenuComponentContext ctx) {
        for (Map.Entry<Class<?>, Object> menu : MENUS.entrySet()) {
            Object m = menu.getValue();
            if (m instanceof IMenuInteraction) {
                ((IMenuInteraction) m).onMenuEvent(ctx);
            }
        }
    }
}
