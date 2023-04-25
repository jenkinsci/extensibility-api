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

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Marks {@link Module}s to be loaded when the world is assembled.
 *
 * <p>
 * When an {@link Injector} is created from a {@link ClassLoader},
 * any extensions that implement this interface will be instantiated
 * with the default constructor, and the module gets {@linkplain Binder#install(Module) installed}.
 *
 * <p>
 * When we are assembling an injector (aka "world") from a set of jar files,
 * those jar files can use this mechanism to insert more sophisticated bindings
 * into the world.
 *
 * <p>
 * This is a pseudo extension point, in the sense that it does not actually
 * produce any binding inside the injector. Instead, it's instantiated while
 * the injector is created, used, then thrown away.
 *
 * @author Kohsuke Kawaguchi
 */
@ExtensionPoint(loader = ExtensionModule.Loader.class)
public interface ExtensionModule extends Module {
    class Loader extends ExtensionLoaderModule<ExtensionModule> {
        @Override
        protected void configure() {
            try {
                install(impl.newInstance());
            } catch (InstantiationException e) {
                throw (Error) new InstantiationError().initCause(e);
            } catch (IllegalAccessException e) {
                throw (Error) new IllegalAccessError().initCause(e);
            }
        }
    }
}
