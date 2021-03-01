package com.mypetproject.mysqlExampleORM;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    boolean primary() default false;
    boolean ai() default false;
    String type() default "int";
    int size() default 11;
}
