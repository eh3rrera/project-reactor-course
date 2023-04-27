---
layout: default
title: Testing for Errors
parent: Testing with StepVerifier
nav_order: 3
---

# Testing for Errors
* * *

Remember that in Reactor, errors are **terminal**, so it makes sense that the methods to consume and test for the expectation of an error are found in the interface `StepVerifier.LastStep`.

For example, to expect an error and consume with the given `Consumer`:
```java
StepVerifier consumeErrorWith(
    Consumer<Throwable> consumer
)
```

To expect an unspecified error:
```java
StepVerifier expectError()
```

To expect an error of the specified type:
```java
StepVerifier expectError(
    Class<? extends Throwable> clazz
)
```

To expect an error and evaluate with the given `Predicate`:
```java
StepVerifier expectErrorMatches(
    Predicate<Throwable> predicate
)
```

To expect an error with the specified message:
```java
StepVerifier expectErrorMessage(
    String errorMessage
)
```

Or to expect an error and assert it via an assertion(s) provided as a `Consumer`:
```java
StepVerifier expectErrorSatisfies(
    Consumer<Throwable> assertionConsumer
)
```

Also, `StepVerifier.LastStep` contains methods to trigger the verification while expecting an error.

For example, to trigger the verification expecting an unspecified error as terminal event:
```java
Duration verifyError()
```

To trigger the verification expecting an error of the specified type as terminal event:
```java
Duration verifyError(
    Class<? extends Throwable> clazz
)
```

To trigger the verification expecting an error that matches the given `Predicate`:
```java
Duration verifyErrorMatches(
    Predicate<Throwable> predicate
)
```

To trigger the verification, expecting an error with the specified message as terminal event:
```java
Duration verifyErrorMessage(
    String errorMessage
)
```

Or to trigger the verification, expecting an error as terminal event, which gets asserted via an assertion(s) provided as a `Consumer`:
```java
Duration verifyErrorSatisfies(
    Consumer<Throwable> assertionConsumer
)
```

Now, suppose we have a sequence that throws an exception when a certain value is filtered. For example, if the following sequence emits a number greater than two, a `RuntimeException` will be thrown:
```java
Flux<Integer> getFlux() {
    return Flux.just(1, 2, 3, 4)
            .filter(i -> i % 2 == 0)
            .map(i -> {
                if(i > 2)
                    throw new 
                        RuntimeException(
                            "Invalid number: " + i
                        );
                return i;
            });
}
```

With the methods of `StepVerifier.LastStep`, we can test this sequence at many levels.

For example, we can have a test scenario where we expect our sequence to terminate with an exception:
```java
StepVerifier
    .create(getFlux())
    .expectNext(2)
    .expectError()
    .verify();
```

Notice that in this case, a method such as `expectComplete()` is not needed, `expectError()` does its job.

However, the above example works with any exception type. If you're expecting a particular type, you can use the following version of `expectError()`:
```java
StepVerifier
    .create(getFlux())
    .expectNext(2)
    .expectError(RuntimeException.class)
    .verify();
```

You can also test for a particular error message with the method `expectErrorMessage`:
```java
StepVerifier
    .create(getFlux())
    .expectNext(2)
    //.expectErrorMessage("Invalid number: ") //Bad
    .expectErrorMessage("Invalid number: 4")  // Good
    .verify();
```

The problem is that, for the test to pass, the message you passed as the argument of this method must be the same that the actual exception message.

But with the method `expectErrorMatches(Predicate)` you can use a `Predicate` to perform custom tests. For example, you can test if the message of the exception starts with a particular string:
```java
StepVerifier
    .create(getFlux())
    .expectNext(2)
    .expectErrorMatches( e -> e.getMessage().startsWith("Invalid number: 4"))
    .verify();
```

All these methods have their corresponding `verifyError` method to test for the expectation and trigger the verification in one step. 

Here's an example:
```java
StepVerifier
    .create(getFlux())
    .expectNext(2)
    .verifyErrorMatches( e -> e.getMessage().startsWith("Invalid number:"));
```
