package com.cloudbees.sdk.extensibility;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Cat extends Animal {
    @Override
    public String bark() {
        return "meow";
    }
}
