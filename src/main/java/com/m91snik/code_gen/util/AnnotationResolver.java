package com.m91snik.code_gen.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * Created by m91snik on 01.12.13.
 */
@Component
public class AnnotationResolver {

    public boolean ignoreAnnotation(Annotation annotation) {
        return Named.class.equals(annotation.annotationType());
    }

}
