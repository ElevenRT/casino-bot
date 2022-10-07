package com.eleven.casinobot.core.interaction.component.button;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonComponentContext implements IButtonComponentContext {

    private final ButtonInteractionEvent event;

    public ButtonComponentContext(ButtonInteractionEvent event) {
        this.event = event;
    }

    @Override
    public Guild getGuild() {
        return event.getGuild();
    }

    @Override
    public ButtonInteractionEvent getEvent() {
        return event;
    }
}
