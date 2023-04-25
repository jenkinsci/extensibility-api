# CloudBees Extensibility API
CloudBees uses Guice as a foundation for our tools and runtime services
to reuse code between them and enable extensibility by 3rd parties.

This library adds basic idiom for defining an extension point and letting other plugins
implement their extensions. (Note that if your extension point can only allow one implementation and not multiple implementations, then the standard Guice binding mechanism should suffice.) Such idiom enables plugins to be developed by unrelated people and still work together at runtime.

For a demonstration of the features of ths library, see [the demo repository](https://github.com/cloudbees/extensibility-api-demo)

## Defining Extension Point
An extension point is an interface/abstract class that represents a contract. I recommend abstract classes where you need to be able to evolve the contract without breaking implementations.

    @ExtensionPoint
    public abstract class Animal {
        public abstract String bark();
    }

Mark your contract with `@ExtensionPoint` to signal that this is an extension point.

## Defining An Extension
An extension is a concrete implementation of an extension point. It needs to extend/implement the extension point type, and it needs to have `@Extension` annotation.

    @Extension
    public class Cat extends Animal {
        @Override
        public String bark() {
            return "meow";
        }
    }

Such a class can exist anywhere, and in fact often lives in separate Maven modules.

## Discovering Extensions
The easiest way to discover extension implementations is to inject `ExtensionList` where it's needed.

    public class AnimalTest {
        @Inject
        ExtensionList<Animal> animals;
        
        ...
        
        public void bark() {
           for (Animal a : animal)
               a.bark();
        }
   }

Alternatively you can programmatically instantiate it and pass in an injector:

    public void foo(Injector injector) {
        for (Animal a : new ExtensionList(Animal.class).list(injector))
            a.bark();
    }

## Wiring up
To let `ExtensionList` discover all the extension implementations, you need to add `ExtensionFinder` module when you create an `Injector`:

    Injector i = Guice.createInjector(new ExtensionFinder(getClass().getClassLoader()));

This module takes a `ClassLoader` as an argument, and any extension implementations that belong to this classloader (and its ancestors) will participate.


## Registering Guice Module from plugins
If a plugin needs to bring in its own Guice `Module`, it can do so by having a `Module` class that implements `ExtensionModule`:

    @Extension
    public class MyModule extends AbstractModule implements ExtensionModule {
        protected void configure() {
            install(new ModuleA());
            install(new ModuleB());
            bind(Foo.class).to(Bar.class);
            ...
        }
    }



