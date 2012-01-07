package com.cloudbees.sdk.extensibility;

import sun.reflect.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;

/**
 * Factory for annotation objects.
 *
 * @author Kohsuke Kawaguchi
 */
public class AnnotationLiteral {
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
