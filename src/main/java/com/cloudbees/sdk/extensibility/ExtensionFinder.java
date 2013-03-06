package com.cloudbees.sdk.extensibility;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.Module;
import org.jvnet.hudson.annotation_indexer.Index;
import org.jvnet.hudson.annotation_indexer.Indexed;

import javax.inject.Named;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

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
            Set<Class> seen = new HashSet<Class>();
            for (Class<?> a : Index.list(ExtensionImplementation.class, cl, Class.class)) {
                if (!a.isAnnotationPresent(Indexed.class))
                    throw new AssertionError(a+" has @ExtensionImplementation but not @Indexed");
                for (Class c : Index.list(a.asSubclass(Annotation.class), cl, Class.class)) {
                    if (seen.add(c)) {// ... so that we don't bind the same class twice
                        for (Class ext : listExtensionPoint(c,new HashSet<Class>())) {
                            bind(c,ext);
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
        lm.init(impl,extensionPoint);
        install(lm);
    }

    /**
     * Creates a new instance of {@link ExtensionLoaderModule} to be used to
     * load the extension of the given type.
     */
    protected  <T> ExtensionLoaderModule<T> createLoaderModule(Class<T> extensionPoint) {
        ExtensionPoint ep = extensionPoint.getAnnotation(ExtensionPoint.class);
        if (ep!=null) {
            if (ep.loader()!=ExtensionLoaderModule.Default.class) {
                try {
                    return (ExtensionLoaderModule)ep.loader().newInstance();
                } catch (InstantiationException e) {
                    throw (Error)new InstantiationError().initCause(e);
                } catch (IllegalAccessException e) {
                    throw (Error)new IllegalAccessError().initCause(e);
                }
            }
        }
        return new ExtensionLoaderModule.Default<T>();
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

