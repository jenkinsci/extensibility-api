package com.cloudbees.sdk.extensibility;

import sun.reflect.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

/**
 * Factory for annotation objects.
 * 
 * Using Guice often requires one to create instances of {@link Annotation} subtypes
 * with values that are only determined at runtime.
 *
 * This factory helps you do that.
 *
 * @author Kohsuke Kawaguchi
 */
public class AnnotationLiteral {
    public static <T extends Annotation> T of(Class<T> type) {
        return of(type,Collections.emptyMap());
    }

    public static <T extends Annotation> T of(Class<T> type, Object value) {
        return of(type,"value",value);
    }

    public static <T extends Annotation> T of(Class<T> type, String key, Object value) {
        return of(type, Collections.singletonMap(key,value));
    }

    public static <T extends Annotation> T of(Class<T> type, final Map<String,?> values) {
        return type.cast(AnnotationParser.annotationForMap(type, (Map) values));
    }
}
