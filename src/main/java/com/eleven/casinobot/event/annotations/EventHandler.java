package com.eleven.casinobot.event.annotations;

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
 * @see com.eleven.casinobot.database.DatabaseTemplate
 * @see java.lang.annotation.Annotation
 * @see ListenerAdapter
 * @author iqpizza6349
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface EventHandler {
}
