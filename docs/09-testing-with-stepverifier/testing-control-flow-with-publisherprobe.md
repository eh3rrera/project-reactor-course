---
layout: default
title: Testing Control Flow with PublisherProbe
parent: Testing with StepVerifier
nav_order: 7
---

# Testing Control Flow with PublisherProbe
* * *

Let's assume we have the following method:
```java
Flux<Integer> processFlux(Flux<Integer> flux) {
    return flux
            .filter(i -> i > 10)
            .defaultIfEmpty(0);
}
```

To test it correctly, we must test two scenarios:
- One where the `filter` operator returns a stream with one or more values and `defaultIfEmpty` is not executed.
- One where the `filter` operator returns an empty stream and `defaultIfEmpty` is executed.

Something like this:
```java
@Test
void example_01_FilterReturnsFlux() {
    StepVerifier.create(
        processFlux(
                Flux.just(5, 12)
        )
    )
    .expectNext(12)
    .verifyComplete();
}

@Test
void example_02_FilterDoesntReturnFlux() {
    StepVerifier.create(
            processFlux(
                    Flux.just(5)
            )
    )
    .expectNext(0)
    .verifyComplete();
}
```

Where, if we pass a sequence that contains `5` and `12`, we should expect `12`. If we only pass `5`, we should expect `0` (the default value).

In this case, we're relying on the value returned by `defaultIfEmpty` to test if this operator was executed.

But what if `defaultIfEmpty` doesn't return a fixed value we can use in our test? 

What if we don't return a value at all?

For example, we can use `switchIfEmpty` to just execute an action and return `Mono<Void>`.

In that case, we'd need a way to know whether a `Publisher` was executed or not due to control flow.

Fortunately, Reactor provides the interface `PublisherProbe`, which acts as an [instrumented](https://stackoverflow.com/a/8755337/3593852) `Publisher` (probe), capturing subscription, cancellation, and request events that can be checked with the methods:
```java
// Returns how many times 
// probe was subscribed
long subscribeCount()

// Returns true if the probe 
// was cancelled to at least once.
boolean wasCancelled()

// Returns true if the probe 
// was requested at least once.
boolean wasRequested()

// Returns true if the probe 
// was subscribed to at least once.
boolean wasSubscribed()
```

In a test scenario, instead of these methods, you can use the following assertion methods:
```java
// To check that the probe was cancelled 
// at least once, or throw an AssertionError.
default void assertWasCancelled()

// To check that the probe was never 
// cancelled, or throw an AssertionError.
default void assertWasNotCancelled()

// To check that the probe was never 
// requested, or throw an AssertionError.
default void assertWasNotRequested()

// To check that the probe was never 
// subscribed to, or throw an AssertionError.
default void assertWasNotSubscribed()

// To check that the probe was requested 
// at least once, or throw an AssertionError.
default void assertWasRequested()

// To check that the probe was subscribed to 
// at least once, or throw an AssertionError.
default void assertWasSubscribed()
```

Now, to create an instance of `PublisherProbe`, you can use:
```java
static <T> PublisherProbe<T> empty()
```

To create a `PublisherProbe` that will simply complete, capturing subscription, cancellation and request events around it.
    
Or:
```java
static <T> PublisherProbe<T> of(
    Publisher<? extends T> source
)
```

To create a `PublisherProbe` out of a `Publisher` that will propagate signals from it while capturing subscription, cancellation and request events around it.

With the instance these methods return, according to your needs, you can use either the method `flux()` that returns a `Flux` version of the probe or `mono()` that returns a `Mono` version of the probe.

Let's review an example using the following method:
```java
Mono<Void> processMono(Mono<Integer> mono, 
                       Mono<Integer> fallback) {
    return mono
        .flatMap(i -> Mono.just(i*10))
        .switchIfEmpty(fallback)
        .flatMap(i -> 
                    Mono.fromRunnable(
                        () -> System.out.println(
                                    "Do something with: " + i)
                    )
        );
}
```

This method returns a `Mono<Void>`, so we cannot test which value the last `flatMap` operator received with any of `StepVerifier`'s `expect*` methods. We'll have to use `PublisherProbe`.

First, we're going to test the path where the fallback value is used, so let's create a `PublisherProbe` with the `Mono` we'll use as fallback using the `of` method:
```java
PublisherProbe<Integer> fallbackProbe = 
        PublisherProbe.of(
                            Mono.just(0)
                        );
```

Then, we're going to pass to `StepVerifier.create` the `Mono` returned by `processMono`. For this method we'll pass `Mono.empty()` as the first parameter, and with `fallbackProbe.mono()` we'll get a `Mono` from the probe as the second parameter:
```java
StepVerifier.create(
        processMono(
                Mono.empty(),
                fallbackProbe.mono()
        )
)
```

Since we're working with a `Mono<Void>`, we're just going to call the `verifyComplete()` method to trigger everything:
```java
StepVerifier.create(
        processMono(
                Mono.empty(),
                fallbackProbe.mono()
        )
)
.verifyComplete();
```

Instead of using `StepVerifier`'s `expect*` methods, we'll use the `PublisherProbe`'s `assert*` methods:
```java
fallbackProbe.assertWasSubscribed();
fallbackProbe.assertWasRequested();
fallbackProbe.assertWasNotCancelled(); // Optional
```

Here's what the complete test looks like:
```java
PublisherProbe<Integer> fallbackProbe = 
            PublisherProbe.of(Mono.just(0));

StepVerifier.create(
        processMono(
                Mono.empty(),
                fallbackProbe.mono()
        )
)
.verifyComplete();

fallbackProbe.assertWasSubscribed();
fallbackProbe.assertWasRequested();
fallbackProbe.assertWasNotCancelled(); // Optional
```

You should get a passing test when you execute it, with the following printed in the console:
```
Do something with: 0
```

Of course, we can use a probe for the first parameter too. For a test where this parameter is used, we'll have to assert that its probe captured the subscribe and request events while asserting the opposite for the fallback probe.

Here's the test that does that:
```java
PublisherProbe<Integer> integerProbe = 
            PublisherProbe.of(Mono.just(1));
PublisherProbe<Integer> fallbackProbe = 
            PublisherProbe.of(Mono.just(0));

StepVerifier.create(
        processMono(
                integerProbe.mono(),
                fallbackProbe.mono()
        )
)
.verifyComplete();

integerProbe.assertWasSubscribed();
integerProbe.assertWasRequested();

fallbackProbe.assertWasNotSubscribed();
fallbackProbe.assertWasNotRequested();
```

Once again, you should get a passing test when you execute it, but this time, the following will be printed in the console:
```
Do something with: 10
```

You can also use a `Flux` by calling `.flux()` instead of `.mono()` or `PublisherProbe.empty()` for cases where you don't need to emit data.
