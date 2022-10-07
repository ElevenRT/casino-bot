package com.eleven.casinobot.core.interaction.component.button;

import net.dv8tion.jda.api.entities.Guild;

import java.util.Set;

public interface IButtonInteraction {

    void onButtonEvent(IButtonComponentContext ctx);

    Set<Long> getInteractioners(Guild guild, String type);
}
