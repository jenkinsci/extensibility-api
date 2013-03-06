package com.cloudbees.sdk.extensibility;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;

import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class ExtensionLoaderModule<T> extends AbstractModule {
    protected Class<? extends T> impl;
    protected Class<T> extensionPoint;

    /**
     * Called by {@link ExtensionFinder} to initialize this module.
     */
    public void init(Class<? extends T> impl, Class<T> extensionPoint) {
        this.impl = impl;
        this.extensionPoint = extensionPoint;
    }

    static class Default<T> extends ExtensionLoaderModule<T> {
        @Override
        protected void configure() {
            Annotation bindingAnnotation = findBindingAnnotation(impl);
            if (bindingAnnotation==null)
               // this is just to make it unique among others that implement the same contract
                bindingAnnotation = AnnotationLiteral.of(Named.class,impl.getName());
            binder().withSource(impl).bind(Key.get(extensionPoint, bindingAnnotation)).to(impl);
            bind(impl);
        }

        private <T> Annotation findBindingAnnotation(Class<? extends T> impl) {
            for (Annotation a : impl.getAnnotations())
                if (a.annotationType().isAnnotationPresent(BindingAnnotation.class))
                    return a;
            return null;
        }
    }
}
