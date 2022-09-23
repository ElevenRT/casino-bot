package com.eleven.casinobot.core.annotations;

import com.eleven.casinobot.core.command.ICommand;
import com.eleven.casinobot.database.AbstractDatabaseTemplate;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event handler annotation is an annotation used to automatically register
 * classes that inherit each event listener into the event context,
 * supporting database templates with constructor injection.
 *
 * @see AbstractDatabaseTemplate
 * @see java.lang.annotation.Annotation
 * @see ListenerAdapter
 * @author iqpizza6349
 * @version 1.0.0
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    Class<? extends ICommand>[] commands() default {};
}
