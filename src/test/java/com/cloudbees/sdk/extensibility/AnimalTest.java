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

import static org.junit.Assert.assertTrue;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import javax.inject.Inject;
import org.junit.Test;

/**
 * @author Kohsuke Kawaguchi
 */
public class AnimalTest {
    @Inject
    ExtensionList<Animal> animals;

    @Test
    public void discovery() {
        Injector i = Guice.createInjector(new ExtensionFinder(getClass().getClassLoader()));
        i.injectMembers(this);
        Animal[] a = Iterables.toArray(animals, Animal.class);
        assertTrue((a[0] instanceof Dog && a[1] instanceof Cat) || (a[1] instanceof Dog && a[0] instanceof Cat));
    }

    /**
     * Make sure we also bind javax.inject.Qualifier annotations
     */
    @Test
    public void javaxInjectBinding() {
        Injector i = Guice.createInjector(new ExtensionFinder(getClass().getClassLoader()));
        assertTrue(i.getInstance(Key.get(Animal.class, Names.named("dog"))) instanceof Dog);
    }
}
