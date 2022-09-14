package com.eleven.casinobot.event.annotations;

import java.lang.annotation.*;

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Injection {
    String name() default "";
}
