---
layout: default
title: Coding Exercises
parent: Testing with StepVerifier
nav_order: 9
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom`, `reactor-test`, and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/09/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/09/before/reactor-demo-exercises)

I'll give you the instructions (and sometimes hints) so you can put all the code together in a method of a test class and make the test pass.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/09/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/09/after/reactor-demo-exercises).

----

## Exercise 1
In this exercise, you'll use `StepVerifier` interface to test a `Publisher` that doesn't terminate but rather times out after the provided duration.
1. Create a unit test class named `Test01` with a method marked with the `@Test` annotation.
2. Create a `Mono` that emits an integer value after `3` seconds.
3. Use the `StepVerifier` to create a test scenario with the [StepVerifier.LastStep.expectTimeout(Duration)](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.LastStep.html#expectTimeout-java.time.Duration-) method to verify that the `Mono` times out after `2` seconds.
4. Run the test scenario and verify the results. The test should pass.

----

## Exercise 2
In this coding exercise, you'll use the `StepVerifier` interface to test if the next element emitted by a `Publisher` matches the given predicate.
1. Create a unit test class named `Test02` with a method marked with the `@Test` annotation.
2. Create a `Flux` that emits a sequence of integers from `1` to `3`.
3. Define a predicate that checks if a number is even.
4. Use StepVerifier to create a test scenario using the [StepVerifier.Step.expectNext(T)](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.Step.html#expectNext-T-) and [StepVerifier.Step.expectNextMatches(Predicate)](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.Step.html#expectNextMatches-java.util.function.Predicate-) methods.
5. Run the test scenario and verify the results. The test should pass.

----

## Exercise 3
In this exercise, you'll use the `StepVerifier` interface to check if the error matches the given predicate.
1. Create a unit test class named `Test03` with a method marked with the `@Test` annotation.
2. Create a `Flux` that emits a sequence of integers from `1` to `3` and concatenate it with another `Flux` that throws an `IllegalStateException` immediately after subscription.
3. Define a `Predicate` that checks if the error is an instance of `IllegalStateException`.
4. Use `StepVerifier` to create a test scenario for the `Flux`, expecting the sequence of integers, and then the exception. For the latter, use the [StepVerifier.LastStep.verifyErrorMatches(Predicate)](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.LastStep.html#verifyErrorMatches-java.util.function.Predicate-) method.
5. Run the test scenario and verify the results. The test should pass.

----

## Exercise 4
In this exercise, you'll use the `StepVerifier` interface to test a `Publisher` by using the [StepVerifier.verifyThenAssertThat()](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.html#verifyThenAssertThat--) method to verify the signals received and then assert the final state.
1. Create a unit test class named `Test04` with a method marked with the `@Test` annotation.
2. Create a `Flux` that emits a sequence of integers from `1` to `3`.
3. Use `StepVerifier` to create a test scenario for the `Flux` using the methods `expectNext` and `expectComplete`.
4. Get an instance of `StepVerifier.Assertions` using the `verifyThenAssertThat` method.
5. Finally, make the following assertions on the final state of the subscriber:
    - No elements were dropped
    - No errors were dropped
    - No elements were discarded
6. Run the test scenario and verify the results. The test should pass.

----

## Exercise 5
In this exercise, you'll use the `StepVerifier` interface with `VirtualTimeScheduler` to test a `Publisher` with virtual time, allowing you to control the passage of time during the test.
1. Create a unit test class named `Test05` with a method marked with the `@Test` annotation.
2. Create a `Flux` that emits a sequence of integers from `1` to `3` with a delay of `1` hour between each element.
3. Use the [StepVerifier.withVirtualTime(Supplier)](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.html#withVirtualTime-java.util.function.Supplier-) and [StepVerifier.Step.thenAwait()](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.Step.html#thenAwait--) methods to create a test scenario for the `Flux`.
4. Run the test scenario and verify the results. The test should pass.

----

## Exercise 6
In this exercise, you'll use [PublisherProbe](https://projectreactor.io/docs/test/release/api/reactor/test/publisher/PublisherProbe.html) to test a `Publisher` and verify that it was canceled at least once.
1. Create a unit test class named `Test06` with a method marked with the `@Test` annotation.
2. Create a `Flux` that emits a sequence of integers from `1` to `5`.
3. Create a `PublisherProbe` instance and use it to wrap the `Flux`.
4. Use the `StepVerifier` to create a test scenario for the wrapped `Flux`, canceling the subscription after receiving the first element with the method `thenCancel()`.
5. Run the test scenario.
6. Use the `PublisherProbe` instance to assert that the probe was canceled. The test should pass.

----

## Exercise 7
In this exercise, you'll use [TestPublisher](https://projectreactor.io/docs/test/release/api/reactor/test/publisher/TestPublisher.html) to create a `Publisher` for testing purposes, and use `StepVerifier` to verify its behavior.
1. Create a unit test class named `Test07` with a method marked with the `@Test` annotation.
2. Create a cold `TestPublisher` instance for integer values.
3. Use the `TestPublisher` to emit a sequence from `1` to `3` using the `next(T, T...)` method.
4. Create a `Flux` from the `TestPublisher`.
5. Use the `StepVerifier` to create a test scenario for the `Flux`. Don't forget to call the `complete()` method on the `TestPublisher`.
6. Run the test scenario and verify the results. The test should pass.

