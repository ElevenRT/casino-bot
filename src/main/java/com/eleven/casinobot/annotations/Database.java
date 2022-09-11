package com.eleven.casinobot.annotations;

import java.lang.annotation.*;

/**
 * An annotation used by the class that inherited the database template class.
 * @see com.eleven.casinobot.database.DatabaseTemplate
 *
 * @author iqpizza6349
 * @version 1.0.0
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Database {
}
