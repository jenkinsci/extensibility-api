/*
 * Copyright 2010-2013, CloudBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.sdk.extensibility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.AnnotationUtils;

/**
 * Factory for annotation objects.
 * <p>
 * Using Guice often requires one to create instances of {@link Annotation} subtypes
 * with values that are only determined at runtime.
 * <p>
 * This factory helps you do that.
 *
 * @author Kohsuke Kawaguchi
 */
public class AnnotationLiteral {
    public static <T extends Annotation> T of(Class<T> type) {
        return of(type, Collections.emptyMap());
    }

    public static <T extends Annotation> T of(Class<T> type, Object value) {
        return of(type, "value", value);
    }

    public static <T extends Annotation> T of(Class<T> type, String key, Object value) {
        return of(type, Collections.singletonMap(key, value));
    }

    public static <T extends Annotation> T of(Class<T> type, final Map<String, Object> values) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
            Annotation annotation = (Annotation) proxy;
            String methodName = method.getName();
            switch (methodName) {
                case "equals":
                    return AnnotationUtils.equals(annotation, (Annotation) args[0]);
                case "toString":
                    return AnnotationUtils.toString(annotation);
                case "hashCode":
                    return AnnotationUtils.hashCode(annotation);
                case "annotationType":
                    return type;
                default:
                    if (!values.containsKey(methodName)) {
                        throw new NoSuchMethodException("Missing value for annotation key: " + methodName);
                    }
                    return values.get(methodName);
            }
        }));
    }
}
