package com.cloudbees.sdk.extensibility;

import org.jvnet.hudson.annotation_indexer.Indexed;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

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
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
@Indexed
public @interface ExtensionImplementation {
}
