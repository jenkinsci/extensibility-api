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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jvnet.hudson.annotation_indexer.Index;
import org.jvnet.hudson.annotation_indexer.Indexed;

/**
 * Guice {@link Module} that discovers {@link ExtensionPoint} implementations and registers them as binding.
 *
 * @author Kohsuke Kawaguchi
 */
public class ExtensionFinder extends AbstractModule {
    private final ClassLoader cl;

    /**
     * @param cl
     *      ClassLoader to find extensions from.
     */
    public ExtensionFinder(ClassLoader cl) {
        this.cl = cl;
    }

    @Override
    protected void configure() {
        try {
            // find all extensions
            Set<Class> seen = new HashSet<>();
            for (Class<?> a : Index.list(ExtensionImplementation.class, cl, Class.class)) {
                if (!a.isAnnotationPresent(Indexed.class)) {
                    throw new AssertionError(a + " has @ExtensionImplementation but not @Indexed");
                }
                for (Class c : Index.list(a.asSubclass(Annotation.class), cl, Class.class)) {
                    if (seen.add(c)) { // ... so that we don't bind the same class twice
                        for (Class ext : listExtensionPoint(c, new HashSet<>())) {
                            bind(c, ext);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Error(e); // fatal problem
        }
    }

    /**
     * Allows the subtype to be selective about what to bind.
     */
    protected <T> void bind(Class<? extends T> impl, Class<T> extensionPoint) {
        ExtensionLoaderModule<T> lm = createLoaderModule(extensionPoint);
        lm.init(impl, extensionPoint);
        install(lm);
    }

    /**
     * Creates a new instance of {@link ExtensionLoaderModule} to be used to
     * load the extension of the given type.
     */
    protected <T> ExtensionLoaderModule<T> createLoaderModule(Class<T> extensionPoint) {
        ExtensionPoint ep = extensionPoint.getAnnotation(ExtensionPoint.class);
        if (ep != null) {
            if (ep.loader() != ExtensionLoaderModule.Default.class) {
                try {
                    return ep.loader().getDeclaredConstructor().newInstance();
                } catch (InstantiationException e) {
                    throw (Error) new InstantiationError().initCause(e);
                } catch (IllegalAccessException e) {
                    throw (Error) new IllegalAccessError().initCause(e);
                } catch (NoSuchMethodException e) {
                    throw (Error) new NoSuchMethodError().initCause(e);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getCause();
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException) t;
                    } else if (t instanceof IOException) {
                        throw new UncheckedIOException((IOException) t);
                    } else if (t instanceof Exception) {
                        throw new RuntimeException(t);
                    } else if (t instanceof Error) {
                        throw (Error) t;
                    } else {
                        throw new Error(e);
                    }
                }
            }
        }
        return new ExtensionLoaderModule.Default<>();
    }

    /**
     * Finds all the supertypes that are annotated with {@link ExtensionPoint}.
     */
    private Set<Class> listExtensionPoint(Class e, Set<Class> result) {
        if (e.isAnnotationPresent(ExtensionPoint.class)) {
            result.add(e);
        }
        Class s = e.getSuperclass();
        if (s != null) {
            listExtensionPoint(s, result);
        }
        for (Class c : e.getInterfaces()) {
            listExtensionPoint(c, result);
        }
        return result;
    }

    private static final Logger LOGGER = Logger.getLogger(ExtensionFinder.class.getName());
}
