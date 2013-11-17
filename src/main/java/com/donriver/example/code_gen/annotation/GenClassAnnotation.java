/**
 * Created by m91snik on 29.09.13.
 */
package com.donriver.example.code_gen.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface GenClassAnnotation {

    String serviceName();

    String[] loggingChannels() default {};
}
