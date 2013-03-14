package com.cloudbees.sdk.extensibility;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import javax.inject.Inject;

import static junit.framework.Assert.assertTrue;

/**
 * @author Kohsuke Kawaguchi
 */
public class AnimalTest {
    @Inject
    ExtensionList<Animal> animals;

    @Test
    public void discovery() {
        Injector i = Guice.createInjector(new ExtensionFinder(getClass().getClassLoader()));
        i.injectMembers(this);
        Animal[] a = Iterables.toArray(animals,Animal.class);
        assertTrue((a[0] instanceof Dog && a[1] instanceof Cat)
                || (a[1] instanceof Dog && a[0] instanceof Cat));
    }
}
