package com.cloudbees.sdk.extensibility;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Dog extends Animal {
    @Override
    public String bark() {
        return "grrr";
    }
}
