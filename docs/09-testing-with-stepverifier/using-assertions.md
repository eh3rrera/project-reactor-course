---
layout: default
title: Using Assertions
parent: Testing with StepVerifier
nav_order: 4
---

# Using Assertions
* * *

In addition to test expectations about the values emitted by a sequence, `StepVerifier.Assertions` allows you to perform additional verifications after the sequence finished successfully.

`StepVerifier.Assertions` has methods to test whether elements were discarded or dropped, if hooks like `onErrorDropped` or `onOperatorError` were called, or to make assertions about the duration of the verification. 

For example, to assert that the tested publisher has discarded at least all of the provided elements to the `discard` hook, in any order:
```java
StepVerifier.Assertions hasDiscarded(
    Object... values
)
```

To assert that the tested publisher has discarded at least one element:
```java
StepVerifier.Assertions hasDiscardedElements()
```

To assert that the tested publisher has dropped at least all of the provided elements, in any order:
```java
StepVerifier.Assertions hasDropped(
    Object... values
)
```

To assert that the tested publisher has dropped exactly one error matching the given predicate:
```java
StepVerifier.Assertions hasDroppedErrorMatching(
    Predicate<Throwable> matcher
)
```
    
To assert that the tested publisher has not discarded any element:
```java
StepVerifier.Assertions hasNotDiscardedElements()
```

To assert that the tested publisher has not dropped any error:
```java
StepVerifier.Assertions hasNotDroppedErrors()
```

To assert that the tested publisher has triggered the `onOperatorError` hook exactly once and the error is of the given type:
```java
StepVerifier.Assertions hasOperatorErrorOfType(
    Class<? extends Throwable> clazz
)
```

To assert that the tested publisher has triggered the `onOperatorError` hook exactly once, with the error message containing the provided string:
```java
StepVerifier.Assertions hasOperatorErrorWithMessageContaining(
    String messagePart
)
```

To assert that the whole verification took strictly less than the provided duration to execute:
```java
StepVerifier.Assertions tookLessThan(
    Duration d
)
```

And to assert that the whole verification took strictly more than the provided duration to execute:
```java
StepVerifier.Assertions tookMoreThan(
    Duration d
)
```

Each category of assertions (for elements discarded, dropped, etc) has methods that take a varargs parameter, a `Predicate`, or a `Consumer`. Check out the [documentation](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.Assertions.html) for more information. 

In any case, to get an instance of `StepVerifier.Assertions`, instead of calling the `verify` method or one of its overloads, we have to call one of the following methods (also from `StepVerifier`) to verify the signals received and then expose assertion methods on the final state:
```java
StepVerifier.Assertions verifyThenAssertThat()

// The duration parameter is the maximum duration to wait 
// for the sequence to terminate, or Duration.ZERO for unlimited wait.
StepVerifier.Assertions verifyThenAssertThat(
    Duration duration
)
```

As you can see, these methods trigger the verification process **and** return an instance of `StepVerifier.Assertions`, so you can call one or more assertion methods.

For example, if we have the following `Flux`:
```java
Flux<Integer> getFlux() {
    return Flux.just(1, 2, 3, 4)
            .delayElements(
                Duration.ofMillis(500)
            )
            .filter(i -> i % 2 == 0);
}
```

We can make assertions about the time it takes to complete and whether elements were dropped or not: 
```java
StepVerifier
    .create(getFlux())
    .expectNextCount(2)
    .expectComplete()
    .verifyThenAssertThat()
    .hasNotDroppedElements()
    .tookLessThan(Duration.ofSeconds(3));
```

We still need to make expectations about the emitted values and whether the sequence completes successfully or not. Assertions are additional tests we make after verifying those expectations.
