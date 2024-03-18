---
layout: default
title: Coding Exercises
parent: Working with map and flatMap
nav_order: 6
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/03/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/03/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/03/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/03/after/reactor-demo-exercises).

----

## Exercise 1
Given a `Flux` of `Integer` values, your task is to transform each value to its hex string representation.
1. First, create a class named `Exercise01` with a `main` method.
2. Create a `Flux` that emits a sequence of `Integer` values. Let's say, from `10` to `14`.
3. Apply the map method to the Flux to transform each emitted value to its hex string representation using something like `String.format("0x%08X", value)`.
4. Subscribe to the transformed `Flux`, printing the emitted items.
5. Run the `Exercise01` class and analyze the output.

----

## Exercise 2
Given a `Mono` that emits an `Integer` value, your task is to transform the emitted value by fetching its square from a separate asynchronous method that returns a `Mono`.
1. First, create a class named `Exercise02` with a `main` method.
2. Create a `Mono` that emits a single `Integer` value.
3. Implement the following method so that it returns a `Mono<Integer>` representing the square of the input number:
    ```java
    private static Mono<Integer> getSquareAsync(Integer value) {
        // TODO: Create a Mono publisher that emits the square of the input number
        return null;
    }
    ```
4. Apply the appropriate operator to the `Mono` created in step 2 to transform the emitted value using the asynchronous method created in step 3.
5. Subscribe to the transformed `Mono`, printing the emitted item.
6. Run the `Exercise02` class and analyze the output.
    
----

## Exercise 3
Given a `Flux` that emits a sequence of `Integer` values, your task is to transform each emitted value into a `Flux` that emits the value and its square.
1. First, create a class named `Exercise03` with a `main` method.
2. Create a `Flux` that emits a sequence of `Integer` values. Let's say from 1 to 5.
3. Implement the following method so that it returns a `Flux<Integer>` that emits the input number and its square:
    ```java
    private static Flux<Integer> getNumberAndSquare(Integer value) {
        // TODO: Create a Flux publisher that emits the input number and its square
        return null;
    }
    ```
4. Apply the appropriate operator to the `Flux` created in step 2 to transform each emitted value by fetching its associated `Flux` using the method created in step 3.
5. Subscribe to the transformed `Flux`, printing the emitted items.
6. Run the `Exercise03` class and analyze the output.

----

## Exercise 4
Modify the previous example so that the method `getNumberAndSquare` returns an `Iterable`, so all elements can be played sequentially:
```java
private static Iterable<Integer> getNumberAndSquare(Integer value) {
    // TODO: Create an Iterable with the input number and its square
}
```


----

## Exercise 5
In this exercise, you'll use the `map` and `flatMap` operators to transform a `Flux` sequence.
1. First, create a class named `Exercise05` with a `main` method.
2. Create a `Flux` that emits a sequence of `Integer` values. Let's say from 1 to 5.
3. Use a `map` operator to triple each emitted value.
4. Next, use a `flatMap` operator to create a new `Flux` that emits the tripled value and its square.
5. Subscribe to the transformed `Flux`, printing the emitted items.
6. Run the `Exercise05` class and analyze the output.
