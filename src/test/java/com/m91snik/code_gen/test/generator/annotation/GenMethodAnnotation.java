/**
 * Created by m91snik on 29.09.13.
 */
package com.m91snik.code_gen.test.generator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface GenMethodAnnotation {

    GenEnum genEnum() default GenEnum.FIRST;

}
