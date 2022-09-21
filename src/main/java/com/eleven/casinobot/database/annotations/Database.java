package com.eleven.casinobot.database.annotations;

import com.eleven.casinobot.database.AbstractDatabaseTemplate;

import java.lang.annotation.*;

/**
 * An annotation used by the class that inherited the database template class.
 * @see AbstractDatabaseTemplate
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Database {
}
