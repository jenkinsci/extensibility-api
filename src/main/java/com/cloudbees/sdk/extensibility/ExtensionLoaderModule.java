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
import com.google.inject.Binding;
import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.inject.Qualifier;

/**
 * Responsible for producing {@link Binding}s inside {@link Injector}
 * from a discovered extension.
 *
 * @author Kohsuke Kawaguchi
 * @see ExtensionPoint
 */
public abstract class ExtensionLoaderModule<T> extends AbstractModule {
    /**
     * The type of the extension implementation discovered.
     */
    protected Class<? extends T> impl;
    /**
     * The type of the extension point.
     */
    protected Class<T> extensionPoint;

    /**
     * Called by {@link ExtensionFinder} to initialize this module.
     */
    public void init(Class<? extends T> impl, Class<T> extensionPoint) {
        this.impl = impl;
        this.extensionPoint = extensionPoint;
    }

    /**
     * The default implementation of {@link ExtensionLoaderModule}.
     * <p>
     * If the discovered implementation has any {@linkplain BindingAnnotation binding annotation},
     * that is used as the key. This allows an extension point that supports named lookup, such as:
     *
     * <pre>
     * &#64;Retention(RUNTIME)
     * &#64;Target(TYPE)
     * &#64;Indexed
     * &#64;BindingAnnotation
     * &#64;ExtensionImplementation
     * public @interface CLICommand {
     *     String value();
     * }
     *
     * &#64;CLICommand("acme")
     * public class AcmeCommand extends Command { ... }
     *
     * // then somewhere else
     * Command cmd = injector.getBinding(Key.get(Command.class,AnnotationLiteral.of(CLICommand.class,"acme"))
     * </pre>
     *
     * If no binding annotation is present, this implementation creates a unique binding
     * annotation, so that at least it can be looked up via {@link ExtensionPointList}.
     */
    static class Default<T> extends ExtensionLoaderModule<T> {
        @Override
        protected void configure() {
            Annotation qa = findQualifierAnnotation(impl);
            if (qa == null) {
                // this is just to make it unique among others that implement the same contract
                qa = AnnotationLiteral.of(Named.class, impl.getName());
            }
            binder().withSource(impl).bind(Key.get(extensionPoint, qa)).to(impl);
            bind(impl);
        }

        private <T> Annotation findQualifierAnnotation(Class<? extends T> impl) {
            for (Annotation a : impl.getAnnotations()) {
                Class<? extends Annotation> at = a.annotationType();
                if (at.isAnnotationPresent(Qualifier.class) || at.isAnnotationPresent(BindingAnnotation.class)) {
                    return a;
                }
            }
            return null;
        }
    }
}
