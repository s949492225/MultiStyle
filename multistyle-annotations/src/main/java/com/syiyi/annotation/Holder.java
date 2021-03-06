package com.syiyi.annotation;

/*
 * viewholder注解
 * Created by songlintao on 2017/1/13.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 编译时注解
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Holder {
    String value() default "default";
}
