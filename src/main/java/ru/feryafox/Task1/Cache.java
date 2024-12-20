package ru.feryafox.Task1;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cache {
    CacheType cacheType() default CacheType.IN_MEMORY;
    boolean zip() default false;
    String fileNamePrefix() default "";
    Class<?>[] identityBy() default {};
    int listLimit() default Integer.MAX_VALUE;
}

