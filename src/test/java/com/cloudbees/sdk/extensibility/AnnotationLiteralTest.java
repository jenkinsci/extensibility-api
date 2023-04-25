/*
 * Copyright 2023, CloudBees Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Named;
import org.junit.Test;

/**
 * @author Basil Crow
 */
public class AnnotationLiteralTest {
    @Test
    public void smokes() {
        Named a = AnnotationLiteral.of(Named.class, "cat");
        assertEquals("@javax.inject.Named(value=cat)", a.toString());
        assertEquals(Named.class, a.annotationType());

        Named cat = Cat.class.getAnnotation(Named.class);
        assertEquals(a, cat);
        assertEquals(a.hashCode(), cat.hashCode());

        Named dog = Dog.class.getAnnotation(Named.class);
        assertNotEquals(a, dog);
        assertNotEquals(a.hashCode(), dog.hashCode());
    }
}
