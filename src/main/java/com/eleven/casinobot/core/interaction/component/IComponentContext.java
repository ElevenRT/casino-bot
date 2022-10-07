package com.eleven.casinobot.core.interaction.component;

import com.eleven.casinobot.core.interaction.IBotContext;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

public interface IComponentContext<ComponentEvent extends GenericComponentInteractionCreateEvent> extends IBotContext<ComponentEvent> {
}
