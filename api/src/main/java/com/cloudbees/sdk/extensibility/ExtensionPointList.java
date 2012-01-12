package com.cloudbees.sdk.extensibility;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * A component you can inject (via JIT binding) to discover the list of
 * extension points registered via {@link ExtensionFinder}.
 *
 * <pre>
 * &#64;Inject
 * ExtensionPointList&lt;Foo> foos;
 *
 * void someFunction(){
 *     for (Foo f : foos) {
 *         ...
 *     }
 * }
 * </pre>
 * @author Kohsuke Kawaguchi
 */
@Singleton
public class ExtensionPointList<T> {
    @Inject
    TypeLiteral<T> type;

    // TODO: if we can inject this like we inject Logger, then
    // we don't need to take injector as a parameter

    public List<T> list(Injector injector) {
        List<T> r = new ArrayList<T>();

        for (Injector i= injector; i!=null; i=i.getParent()) {
            for (Entry<Key<?>, Binding<?>> e : i.getBindings().entrySet()) {
                if (e.getKey().getTypeLiteral().equals(type))
                    r.add((T)e.getValue().getProvider().get());
            }
        }
        return r;
    }
}
