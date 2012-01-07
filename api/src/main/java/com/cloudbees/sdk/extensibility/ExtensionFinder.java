package com.cloudbees.sdk.extensibility;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import org.jvnet.hudson.annotation_indexer.Index;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public class ExtensionFinder extends AbstractModule {
    private final ClassLoader cl;
    private final Set<Class> extensionPoints = new HashSet<Class>();

    public ExtensionFinder(ClassLoader cl) throws IOException {
        this.cl = cl;

        for (Class c : Index.list(ExtensionPoint.class, cl, Class.class))
            extensionPoints.add(c);
    }
    
    public void addExtensionPoint(Class c) {
        extensionPoints.add(c);
    }

    @Override
    protected void configure() {
        try {
            // find all the extension points and build up multibinders
            Map<Class,Multibinder> binders = new HashMap<Class, Multibinder>();
            for (Class c : extensionPoints) {
                Multibinder<?> mbind = Multibinder.newSetBinder(binder(), c);
                binders.put(c,mbind);
            }

            // find all extensions
            for (Class c : Index.list(Extension.class, cl, Class.class)) {
                bind(c,c,binders.get(c));
            }
        } catch (IOException e) {
            throw new Error(e); // fatal problem
        }
    }

    /**
     * Allows the subtype to be selective about what to bind.
     */
    protected <T> void bind(Class<? extends T> impl, Class<T> extensionPoint, Multibinder<T> mbinder) {
        Annotation bindingAnnotation = findBindingAnnotation(impl);
        mbinder.addBinding().to(
                bindingAnnotation!=null
                ? Key.get(impl,bindingAnnotation)
                : Key.get(impl));
    }

    private <T> Annotation findBindingAnnotation(Class<? extends T> impl) {
        for (Annotation a : impl.getAnnotations())
            if (a.getClass().isAnnotationPresent(BindingAnnotation.class))
                return a;
        return null;
    }

    /**
     * Finds all the supertypes that are annotated with {@link ExtensionPoint}.
     */
    private Set<Class> listExtensionPoint(Class e, Set<Class> result) {
        if (e.isAnnotationPresent(ExtensionPoint.class))
            result.add(e);
        Class s = e.getSuperclass();
        if (s!=null)
            listExtensionPoint(s,result);
        for (Class c : e.getInterfaces()) {
            listExtensionPoint(c,result);
        }
        return result;
    }

    private static final Logger LOGGER = Logger.getLogger(ExtensionFinder.class.getName());
}

