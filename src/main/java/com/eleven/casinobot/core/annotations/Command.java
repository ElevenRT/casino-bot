package com.eleven.casinobot.core.annotations;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command annotations are annotations that support specifying each command class.
 * This command class must be a class that implements ICommand.
 * @see SlashCommandInteractionEvent
 * @see Component
 * @author iqpizza6349
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Specify the value to respond to in the Event
     * @return specify value
     */
    String value();

    /**
     * Returns a description of the command.
     * @return description of the command
     */
    String description();

    CommandDetail detail() default @CommandDetail;
}
