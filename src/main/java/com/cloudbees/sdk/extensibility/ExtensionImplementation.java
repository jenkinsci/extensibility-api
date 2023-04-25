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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jvnet.hudson.annotation_indexer.Indexed;

/**
 * Marks annotations that indicate implementations of extension points,
 * such as {@link Extension}.
 *
 * <p>
 * We could have required that we put {@link Extension} on all of those,
 * but letting other annotations serve that role would reduce the # of
 * annotations the user would have to write on their class.
 *
 * <p>
 * Annotations annotated with {@link ExtensionImplementation} must also
 * need to be annotated with {@link Indexed} because that's how we
 * enumerate them.
 *
 * @author Kohsuke Kawaguchi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Indexed
public @interface ExtensionImplementation {}
