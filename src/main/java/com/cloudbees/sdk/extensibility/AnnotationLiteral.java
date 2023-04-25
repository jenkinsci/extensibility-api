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
}
