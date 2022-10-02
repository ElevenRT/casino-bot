package com.eleven.casinobot.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDetail {

    /**
     * configuration command type
     * default command type is {@code Type.COMMAND}
     * @return type of the command
     */
    Type commandType() default Type.COMMAND;

    String gameType() default "";

    String[] interactionIds() default {};

    enum Type {
        /**
         * It is a basic type of general command type.
         */
        COMMAND,

        /**
         * It is a type that adds game functions to the general command type.
         */
        GAME
    }
}
