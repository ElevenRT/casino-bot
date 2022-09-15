package com.eleven.casinobot.context.annotations;

import java.lang.annotation.*;

/**
 * Injection annotation is using for field injection.
 * support for Database Template field.
 * Currently, it has been defined as a principle to inject the constructor.
 * It left the constructor for use when it was difficult to make.
 *
 * @see com.eleven.casinobot.database.DatabaseTemplate
 * @see java.lang.annotation.Annotation
 * @author iqpizza6349
 * @version 1.0.0
 */
@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Injection {
    String name() default "";
}
