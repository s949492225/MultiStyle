package com.syiyi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据列数生成不同列数的GalleryHolder
 * Created by mac on 17/2/24.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ColumnCount
{
    int value();
}
