package com.eleven.casinobot.core.interaction.component.menu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

public class MenuComponentContext implements IMenuComponentContext {

    private final SelectMenuInteractionEvent event;

    public MenuComponentContext(SelectMenuInteractionEvent event) {
        this.event = event;
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public SelectMenuInteractionEvent getEvent() {
        return event;
    }
}
