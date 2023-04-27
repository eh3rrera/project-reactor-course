---
layout: default
title: Testing Time-based Operators
parent: Testing with StepVerifier
nav_order: 6
---

# Testing Time-based Operators
* * *

How do you test a sequence that takes some time to complete?

![Time](images/104.png)

Take the following sequence as an example:
```java
Flux.just(1, 2, 3, 4)
        .delayElements(Duration.ofMinutes(1));
```

It emits one value every minute.

Probably, you can wait four minutes for your test to finish.

But what if this sequence emits a value every hour?

What if this sequence emits thousands of values?

What if we can't speed up the sequence?

Well, the only solution would be to eliminate the time factor somehow.

As you have learned previously, time-based operators like `delayElements` don't work on the default `Scheduler`, they work in a parallel `Scheduler`.

So if we could use a mock `Scheduler` to run these time-based operators, we could manipulate it to make these operators more testable.

Fortunately, `StepVerifier` provides a method that does just that, `withVirtualTime`, which prepares a new `StepVerifier` in a controlled environment using `VirtualTimeScheduler` to manipulate a virtual clock via `StepVerifier.Step.thenAwait()`:
```java
static <T> StepVerifier.FirstStep<T> withVirtualTime(
    Supplier<? extends Publisher<? extends T>> s
)

// n is the amount of items to request (it must be >= 0).
static <T> StepVerifier.FirstStep<T> withVirtualTime(
    Supplier<? extends Publisher<? extends T>> s, 
    long n
)

// The options parameter can include the supplier of the
// VirtualTimeScheduler to inject. Otherwise, this method will
// make a copy of said options and set up the default supplier.
static <T> StepVerifier.FirstStep<T> withVirtualTime(
    Supplier<? extends Publisher<? extends T>> s, 
    StepVerifierOptions options
)

// vtsLookup is the supplier of the VirtualTimeScheduler 
// to inject during verification.
// n is the amount of items to request (it must be >= 0).
static <T> StepVerifier.FirstStep<T> withVirtualTime(
    Supplier<? extends Publisher<? extends T>> s, 
    Supplier<? extends VirtualTimeScheduler> vts, 
    long n
)
```

This method replaces the default `Scheduler` instances with a single instance of a [VirtualTimeScheduler](https://projectreactor.io/docs/test/release/api/reactor/test/scheduler/VirtualTimeScheduler.html), which uses a virtual clock that allows manipulating time in tests.

To use this `Scheduler`, you should lazily build your sequence inside a `Supplier` passed to the method `withVirtualTime`, so any operator created within the `Supplier` without a specific scheduler can use the `VirtualTimeScheduler`.

The number of values to request initially can be passed as an argument of the method or set in the options with the following method of the [StepVerifierOptions](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifierOptions.html) class:
```java
StepVerifierOptions initialRequest(
    long initialRequest
)
```

Otherwise, `StepVerifier` will request an unbounded amount of values at verification time.

In any case, the interface `StepVerifier.Step` provides two expectation methods related to time:
```java
// To pause the expectation evaluation for a given Duration.
StepVerifier.Step<T> thenAwait(Duration timeshift)

// To expect that no event's been observed by the verifier 
// for the length of the provided Duration.
StepVerifier.Step<T> expectNoEvent(Duration duration)
```

You can advance the virtual time clock with the method `thenAwait(Duration)`. Usually, this method pauses the expectation evaluation for a given `Duration`, but if a `VirtualTimeScheduler` has been configured, it will call the method `VirtualTimeScheduler.advanceTimeBy(Duration)` and the pause will not block testing or the executing thread.

Or you can also call `expectNoEvent(Duration)`, to expect that no event has been observed by the verifier for the length of the provided `Duration` (using the virtual clock). However, you should only use this method as the first expectation if you don't expect a subscription. Otherwise, use `StepVerifier.FirstStep.expectSubscription()` along with `expectNoEvent(Duration)`, because most of the time, there will be at least a subscription event even though the clock hasn't advanced.

Let's review all these concepts with an example.

Assuming we have the following sequence that takes twenty hours to complete:
```java
Flux<Integer> getFlux() {
    return Flux.just(1, 2, 3, 4)
        .delayElements(
            Duration.ofHours(5)
        );
}
```

We can get the `Flux` in a lambda expression that we'll pass to the method `withVirtualTime`:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
```

However, if we just add our expectation and call `verifyComplete()`, for example:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
    .expectNextCount(4)
    .verifyComplete();
```

The test will hang. It's not enough to use the virtual clock, we have to advance it with the method `thenAwait(Duration)`:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
    .thenAwait(Duration.ofHours(20))
    .expectNextCount(4)
    .verifyComplete();
```

The above test should pass because after twenty hours, the `Flux` should emit four elements.

Since all `verify` methods return a `Duration` value, we can save and print this value to see how much the test really took:
```java
Duration d = StepVerifier
    .withVirtualTime(() -> getFlux())
    .thenAwait(Duration.ofHours(20))
    .expectNextCount(4)
    .verifyComplete();

System.out.println(d.toMillis());
```

In my case, it was around one hundred milliseconds.

Also, we can add more expectations. For example, to make sure that nothing happened earlier than it should have, we can use the method `expectNoEvent(Duration)`:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
    .expectNoEvent(Duration.ofHours(5))
    .thenAwait(Duration.ofHours(20))
    .expectNextCount(4)
    .verifyComplete();
```

With `expectNoEvent(Duration.ofHours(5))` we're saying that for the first five hours, the `Flux` shouldn't emit any signal. However, if we run the test, it will fail, because there's a subscription event at the beginning. For this reason, `expectNoEvent(Duration)` is almost always preceded by `expectSubscription()`:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
    .expectSubscription()
    .expectNoEvent(Duration.ofHours(5))
    .thenAwait(Duration.ofHours(20))
    .expectNextCount(4)
    .verifyComplete();
```

This time the test should pass, even though we're waiting a total of 25 hours before calling the `expectNextCount(4)` method. 

If you want to focus only on the values emitted, the following test should be enough:
```java
StepVerifier
    .withVirtualTime(() -> getFlux())
    .thenAwait(Duration.ofHours(20))
    .expectNext(1, 2, 3, 4)
    .verifyComplete();
```

And, based on [this Stack Overflow answer](https://stackoverflow.com/a/66890479/3593852), we can generate a list of values and use `expectNextSequence(Iterable)` for cases where the `Flux` emits many values:
```java
List<Integer> list = IntStream.rangeClosed(1, 4)
    .boxed()
    .collect(Collectors.toList());

StepVerifier
    .withVirtualTime(() -> getFlux())
    .thenAwait(Duration.ofHours(20))
    .expectNextSequence(list)
    .verifyComplete();
```

This works because `thenAwait(Duration)` pauses the expectation evaluation for a given `Duration`.

If you use the method `expectNoEvent(Duration)` instead of `thenAwait(Duration)`:
```java
List<Integer> list = IntStream.rangeClosed(1, 4)
    .boxed()
    .collect(Collectors.toList());

StepVerifier
    .withVirtualTime(() -> getFlux())
    .expectSubscription()
    .expectNoEvent(Duration.ofHours(20))
    .expectNextSequence(list)
    .verifyComplete();
```

The test will fail:
```
java.lang.AssertionError: expectation failed (expected no event: onNext(1))
	at reactor.test.MessageFormatter.assertionError(MessageFormatter.java:115)
    ...
```

The reason is that the first element is emitted after five hours but `expectNoEvent(Duration)` expects that in twenty hours no event happens. 

Also, according to the documentation of `expectNoEvent(Duration)`:
> ... avoid using this method at the end of the set of expectations: prefer `expectTimeout(Duration)` rather than `expectNoEvent(...).thenCancel()`.
