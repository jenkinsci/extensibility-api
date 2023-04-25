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
 * ExtensionList&lt;Foo&gt; foos;
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
public class ExtensionList<T> implements Iterable<T> {
    private final TypeLiteral<T> type;

    @Inject
    private Injector injector;

    // TODO: if we can inject this like we inject Logger, then
    // we don't need to take injector as a parameter

    @Inject
    public ExtensionList(TypeLiteral<T> type) {
        this.type = type;
    }

    public ExtensionList(Class<T> type) {
        this(TypeLiteral.get(type));
    }

    /**
     * If {@link ExtensionList} is injected, then it can be used as
     * {@link Iterable} to list up extensions that are found in that injector.
     */
    public Iterator<T> iterator() {
        if (injector==null)
            throw new IllegalArgumentException();
        return list(injector).iterator();
    }

    /**
     * Returns all the extension implementations in the specified injector.
     */
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
