---
layout: default
title: Coding Exercises
parent: Using a Context
nav_order: 4
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/07/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/07/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/07/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/07/after/reactor-demo-exercises).

----

## Exercise 1
In this coding exercise, you'll create a `Flux` that uses the [contextWrite(ContextView)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#contextWrite-reactor.util.context.ContextView-) method to enrich the context with additional data.
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise01 {
        public static void main(String[] args) {
            Flux<Integer> numbers = Flux.range(1, 5);
            String key = "divider";
            double value = 10.0;
            double defaultValue = 1.0;

            // TODO: Divide each emitted number by the "divider" value from the context
            Flux<Double> contextualizedNumbers = null;

            // TODO: Add to the Context the key-value pair with the variables of the same name
            // TODO: Subscribe to contextualizedNumbers
        }
    }
   ```
2. Use the `transformDeferredContextual` method to divide each emitted number of `numbers` by the `"divider"` value from the context, using the default value defined by the variable `defaultValue`.
3. Use the `contextWrite(ContextView)` method to enrich the context with a key-value pair, using the variables `key` and `value`.
4. Subscribe to `contextualizedNumbers` printing the emitted values.
5. Run the `Exercise01` class and analyze the output.

----

## Exercise 2
In this coding exercise, you'll create a `Flux` that uses the [contextWrite(Function<Context, Context>)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#contextWrite-java.util.function.Function-) method to enrich the context with additional data.
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise02 {
        public static void main(String[] args) {
            Flux<Integer> numbers = Flux.range(1, 5);
            String key = "multiplier";
            int value = 3;
            int defaultValue = 1;

            // TODO: Multiply each emitted number by the "multiplier" value from the context
            Flux<Integer> contextualizedNumbers = null;

            //  TODO: Add to the Context the key-value pair with the variables of the same name
            //  TODO: Subscribe to contextualizedNumbers
        }
    }
   ```
2. Use `flatMap` and `Mono.deferContextual` to multiply each emitted number of `numbers` by the `"multiplier"` value from the context, using the default value defined by the variable `defaultValue`.
3. Use the `contextWrite(Function<Context, Context>)` method to enrich the context with a key-value pair, using the variables `key` and `value`.
4. Subscribe to `contextualizedNumbers` printing the emitted values.
5. Run the `Exercise02` class and analyze the output.

----

## Exercise 3
In this coding exercise, you'll use the [transformDeferredContextual](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#transformDeferredContextual-java.util.function.BiFunction-) method to apply a transformation to a `Mono` while considering the context. 
1. Given the following code:
   ```java
    import reactor.core.publisher.Mono;

    public class Exercise03 {
        public static void main(String[] args) {
            Mono<String> greetingMono = Mono.just("Hello");
            String key = "username";

            // TODO: Append the value of the "username" key from the context to the emitted string
            Mono<String> contextualizedGreetingMono = null;

            //  TODO: Add to the Context the value "Alice"
            //  TODO: Subscribe to contextualizedNumbers

            //  TODO: Add to the Context the value "Bob"
            //  TODO: Subscribe again to contextualizedNumbers
        }
    }
   ```
2. Use `transformDeferredContextual` to append the value of the `"username"` key from the context to the string emitted by `greetingMono`.
3. Use the `contextWrite(Function<Context, Context>)` method to enrich the context with a key-value pair, using the variable `key` as key, and `"Alice"` as value.
4. Subscribe to `contextualizedNumbers` printing the emitted value.
5. One more time, use the `contextWrite(Function<Context, Context>)` method to enrich the context with a key-value pair, using the variable `key` as key, and `"Bob"` as value.
6. Subscribe again to `contextualizedNumbers` printing the emitted value.
7. Run the `Exercise03` class and analyze the output.

