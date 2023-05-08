---
layout: default
title: Coding Exercises
parent: Working with Mono and Flux
nav_order: 5
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/02/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/02/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/02/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/02/after/reactor-demo-exercises).

----

## Exercise 1
This exercise will test your knowledge of the [Mono.justOrEmpty](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#justOrEmpty-java.util.Optional-) method. You'll create a simple application that demonstrates its usage with two `Optional` values.
1. Create a class named `Exercise01` with a `main` method.
2. Create two `Optional` variables, one with a value and one empty.
3. Use the `Mono.justOrEmpty` method to create two `Mono` instances, one for each `Optional`.
4. Subscribe to both `Mono` instances and print the results using the following code:
    ```java
    monoWithValue.subscribe(
        element -> System.out.println("Mono with value - Value: " + element),
        error -> System.err.println("Mono with value - Error: " + error.getMessage()),
        () -> System.out.println("Mono with value complete")
    );

    monoEmpty.subscribe(
        element -> System.out.println("Mono empty - Value: " + element),
        error -> System.err.println("Mono empty - Error: " + error.getMessage()),
        () -> System.out.println("Mono empty complete")
    );
    ```
5. Run the `Exercise01` class and analyze the output. Which ones of the above print statements are executed? Why?

----

## Exercise 2
In this exercise, you will create a `Publisher` that emits more than one value and use the [Mono.fromDirect](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromDirect-org.reactivestreams.Publisher-) method to convert it to a `Mono`.
1. Given the following class:
    ```java
    import org.reactivestreams.Publisher;
    import reactor.core.publisher.Mono;

    public class Exercise02 {
        public static void main(String[] args) {
            // Create a simple Publisher
            Publisher<Integer> publisher = createPublisher();

            // Convert the Publisher to a Mono using fromDirect()
            Mono<Integer> mono = Mono.fromDirect(publisher);

            // Subscribe to the Mono
            mono.subscribe(
                    value -> System.out.println("Received: " + value),
                    error -> System.err.println("Error: " + error),
                    () -> System.out.println("Completed")
            );
        }

        private static Publisher<Integer> createPublisher() {
            // TODO: Create a Flux publisher that emits two values
            return null;
        }
    }
    ```
2. Implement the `createPublisher()` method so that it creates and returns a `Flux` that emits two elements.
3. Run the `Exercise02` class and analyze the output. Does it compile and run correctly? How many elements are printed? Why?

----

## Exercise 3
In this exercise, you will create a `CompletableFuture` that emits a value and use the [Mono.fromFuture](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromFuture-java.util.concurrent.CompletableFuture-) method to convert it to a `Mono`.
1. First, create create a class named `Exercise03` with a `main` method.
2. Add the following method to the class (also adding the corresponding `import` statements):
    ```java
    private static CompletableFuture<String> createCompletableFuture() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> "Hi!", executor);
    }
    ```
3. With the instance returned by this method, create a `Mono` in the `main` method, and subscribe to it to print the value wrapped in the `CompletableFuture` instance.
4. Run the `Exercise03` class and see the output.

**Note:** In a real-world application the executor service of the method `createCompletableFuture` must be properly shut down. One option is creating the executor service in the `main` method, passing it as an argument of `createCompletableFuture`, and shut it down after the `Mono` completes.

----

## Exercise 4
In this exercise, you will create a `Supplier` that provides a `Publisher` and use the [Flux.defer](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#defer-java.util.function.Supplier-) method to lazily instantiate the `Publisher` every time a subscription is made on the resulting `Flux`.
1. First, create create a class named `Exercise04` with a `main` method.
2. Add the following method to the class (also adding the corresponding `import` statement):
    ```java
    private static Supplier<Publisher<String>> createPublisherSupplier() {
        return () -> Flux.just("Current time: " + System.currentTimeMillis());
    }
    ```
3. In the `main` method, create a `Flux` using the supplier returned by the above method.
4. Subscribe to the `Flux` and print the value emitted.
5. Once again, subscribe to the `Flux` and print the value emitted.
6. Run the `Exercise04` class and analyze the output. Does it throw an exception when you subscribe the second time? Why or why not?

----

## Exercise 5
In this exercise, you will create a simple `Flux` that emits values and use the [subscribe(CoreSubscriber<? super T> actual)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#subscribe-reactor.core.CoreSubscriber-) method to subscribe to the `Flux` using a custom `BaseSubscriber`.
1. Given the following classes:
    ```java
    import org.reactivestreams.Publisher;
    import reactor.core.publisher.Mono;

    public class Exercise05 {
        public static void main(String[] args) {
            // Create a simple Flux that emits values
            Flux<Integer> flux = Flux.just(1, 2, 3);

            // Create a custom BaseSubscriber
            CustomBaseSubscriber<Integer> customSubscriber = new CustomBaseSubscriber<>();

            // Subscribe to the Flux using the custom BaseSubscriber
            flux.subscribe(customSubscriber);
        }
    }

    class CustomBaseSubscriber<T> extends BaseSubscriber<T> {
    }
    ```
2. Implement the methods of the class `CustomBaseSubscriber` so that when you run the class `Exercise05`, it prints the following:
    ```
    Subscribed
    Received: 1
    Received: 2
    Received: 3
    Completed
    ```
**Hint:** `CustomBaseSubscriber` has to override the methods: `hookOnSubscribe`, `hookOnNext`, `hookOnError`, and `hookOnComplete`.
