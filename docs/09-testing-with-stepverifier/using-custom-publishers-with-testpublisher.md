---
layout: default
title: Using Custom Publishers in Tests with TestPublisher
parent: Testing with StepVerifier
nav_order: 8
---

# Using Custom Publishers in Tests with TestPublisher
* * *

Reactor provides the class [TestPublisher](https://projectreactor.io/docs/test/release/api/reactor/test/publisher/TestPublisher.html) to have complete control over a `Publisher` you want to test. This is useful for advanced test cases such as:
- When you want to trigger specific signals at a particular moment.
- When you implement a custom operator and want to check if it follows the [Reactive Streams specification](https://www.reactive-streams.org/).

`TestPublisher` implements the interfaces `Publisher` and `PublisherProbe`:
```java
public abstract class TestPublisher<T> 
            implements Publisher<T>, PublisherProbe<T> {
    // ...
}
```

This way, `TestPublisher` can act as a `Publisher`, having methods to trigger signals such as:
```java
// To trigger completion of this publisher.
TestPublisher<T> complete()

// To combine emitting items, 
// completing the publisher.
TestPublisher<T> emit(T... values)

// To trigger an error signal to the subscribers.
TestPublisher<T> error(Throwable t)

// To send one onNext signal to the subscribers.
TestPublisher<T> next(T value)

// To send one to many onNext signals
// to the subscribers.
TestPublisher<T> next(T first, T... rest)
```

From `PublisherProbe`, you'll have at your disposal the methods we previously reviewed for this interface, such as `assertWasRequested()`, `assertWasSubscribed()`, `assertWasNotCancelled()`, `subscribeCount()`, `mono()`, and `flux()`. However, `PublisherProbe` also adds methods to perform many types of assertions.

For example, to assert that the publisher has had at least one subscriber that has been cancelled:
```java
TestPublisher<T> assertCancelled()
```

To assert that the publisher has had at least `n` subscribers that have been cancelled:
```java
TestPublisher<T> assertCancelled(int n)
```

To assert that the current maximum request of all this publisher's subscribers is less than or equal to `n`:
```java
TestPublisher<T> assertMaxRequested(long n)
```

To assert that the current minimum request of all this publisher's subscribers is greater than or equal to `n`:
```java
TestPublisher<T> assertMinRequested(long n)
```

To assert that this publisher has had no subscriber with request overflow:
```java
TestPublisher<T> assertNoRequestOverflow()
```

To assert that this publisher has no subscribers:
```java
TestPublisher<T> assertNoSubscribers()
```

To assert that this publisher has had no cancelled subscribers:
```java
TestPublisher<T> assertNotCancelled()
```

To assert that this publisher has had a subscriber that saw request overflow, that has received an `onNext` event despite having a requested amount of `0` at the time:
```java
TestPublisher<T> assertRequestOverflow()
```

To assert that this publisher has subscribers:
```java
TestPublisher<T> assertSubscribers()
```

Or to assert that this publisher has exactly `n` subscribers:
```java
TestPublisher<T> assertSubscribers(int n)
```

You can create an instance of `TestPublisher` with a variety of static methods.

For example, the following method creates a standard hot `TestPublisher`:
```java
static <T> TestPublisher<T> create()
```

The following method creates a cold `TestPublisher`, which can be subscribed to by multiple subscribers, holding off emitting elements from a buffer if the subscriber doesn't have enough request:
```java
static <T> TestPublisher<T> createCold()
```

This method creates a cold `TestPublisher`, which can be subscribed to by multiple subscribers, throwing an overflow error if a new subscriber's first request is lower than the current buffer size, or if a new element is pushed to a registered subscriber that has zero pending demand:
```java
static <T> TestPublisher<T> createColdNonBuffering()
```

This other method creates a cold `TestPublisher` non-compliant with the spec according to one or more `TestPublisher.Violations`. `errorOnOverflow` indicates whether to throw an exception if there are more values than the ones requested (`true`) or buffer values until request becomes available (`false`):
```java
static <T> TestPublisher<T> createColdNonCompliant(
    boolean errorOnOverflow, 
    TestPublisher.Violation firstViolation, 
    TestPublisher.Violation... otherViolations
)
```

And this method creates a non-compliant hot `TestPublisher` with a given set of reactive streams spec violations that will be overlooked:
```java
static <T> TestPublisher<T> createNoncompliant(
    TestPublisher.Violation first, 
    TestPublisher.Violation... rest
)
```

As you can see, there are two types of publishers, *hot* and *cold*. For this reason, there are three types of `TestPublisher` instances you can create:
- Hot
- Cold
- Non-compliant (according to the [Reactive Streams specification](https://www.reactive-streams.org/))

Until now, we've been working with *cold* publishers:
- Nothing happens until there's a subscriber
- Each subscriber receives all the values emitted from the beginning.

For example, given the following `Flux`:
```java
Flux<Integer> flux = Flux.just(1, 2, 3, 4);
```

If one client subscribes to the `Flux` and then, at the same time the third value is emitted, a second client subscribes to the `Flux`, both subscribers would receive all the emitted elements:
```
Subscriber 1: 1
Subscriber 1: 2
Subscriber 1: 3
Subscriber 1: 4
Subscriber 2: 1
Subscriber 2: 2
Subscriber 2: 3
Subscriber 2: 4
```

On the other hand, a *hot* can start publishing data right away, without any subscribers. When a client subscribes to the *hot* publisher, it will only receive the elements emitted after it subscribed.

In other words, with a *hot* publisher:
- Something can happen before there's a subscriber
- Each subscriber receives the values emitted after it subscribed.

Applying the previous example to a *hot* publisher, if the first client subscribed before (or at the same time) the `Flux` started emitting value and the second client subscribed before (or at the same time) the `Flux` emitted the third value, this will be the result:
```
Subscriber 1: 1
Subscriber 1: 2
Subscriber 1: 3
Subscriber 2: 3
Subscriber 1: 4
Subscriber 2: 4
```

`Subscriber 1` receives all four values. 

`Subscriber 2` only receives the last two values.

It's important to know the difference because using a *hot* `TestPublisher` is different from using a *cold* `TestPublisher`.

Consider the following example:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.create();
testPublisher.next(1);

StepVerifier.create(testPublisher.flux())
    .expectNext(1)
    .verifyComplete();
```

First, we create an instance of `TestPublisher` with the method `create()`.

Then, we emit the value `1` with the method `next()`.

Once we have our `TestPublisher` instance set up, we get a `Flux` from it so we can test its behavior with `StepVerifier`.

However, when we run the test, it will hang indefinitely.

The reason is that `create()` method returns a *hot* `TestPublisher`, so when we call the `next(1)` method, we're emitting the value at that time. This way, `StepVerifier` will block indefinitely, waiting for another value or complete signal that will never come.

To avoid this, instead of `verifyComplete()`, we can use `verifyTimeout(Duration)`, passing a timeout of one second, for example:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.create();
testPublisher.next(1);

StepVerifier.create(testPublisher.flux())
    .expectNext(1)
    //.verifyComplete();
    .verifyTimeout(Duration.ofSeconds(1));
```

To make the test pass, we could create a *cold* `TestPublisher` with `createCold()`:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.createCold();
testPublisher.next(1);

StepVerifier.create(testPublisher.flux())
    .expectNext(1)
    //.verifyComplete();
    .verifyTimeout(Duration.ofSeconds(1));
```

Of course, since `TestPublisher` implements `PublisherProbe`, we can call some assertion methods too:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.createCold();
testPublisher.next(1);

StepVerifier.create(testPublisher.flux())
    .expectNext(1)
    //.verifyComplete();
    .verifyTimeout(Duration.ofSeconds(1));

testPublisher.assertWasSubscribed();
testPublisher.assertWasRequested();
```

This time, the test should pass because a cold publisher doesn't emit a value until someone subscribes to it (in this case, `StepVerifier`).

To properly use a *hot* `TestPublisher`, we need to use the method `then(Runnable)`, from the interface `StepVerifier.Step`:
```java
// To run a task scheduled after the previous expectations or tasks.
StepVerifier.Step<T> then(Runnable task)
```

Inside the `Runnable` we pass to this method, `TestPublisher` can emit values or make assertions, for example.

Something like this:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.create();

StepVerifier.create(testPublisher.flux())
    .then(() -> {
        testPublisher.assertWasSubscribed();
        testPublisher.next(1);
    })
    .expectNext(1)
    .then(() -> testPublisher.complete())
    .verifyComplete();

testPublisher.assertNoSubscribers();
```

Where, inside the `Runnable` passed to the method `then`, we assert that there's a subscription and emit a value, which is tested with the `expectNext` method.

This time, as the `TestPublisher` is marked as completed with `then(() -> testPublisher.complete())`, we can use `verifyComplete()` and even make more assertions about the state of the `TestPublisher` instance.

Now, about non-compliant publishers, the methods `createNoncompliant` and `createColdNoncompliant` allow you to create publishers that don't follow certain parts of the [Reactive Streams specification](https://www.reactive-streams.org/). These parts are defined by the values of the [TestPublisher.Violation enum](https://projectreactor.io/docs/test/release/api/reactor/test/publisher/TestPublisher.Violation.html), which are:
```java
// Allow next calls to be made with a null value 
// without triggering a NullPointerException
ALLOW_NULL

// Allow termination signals to be sent several times in a row, 
// including: 
//   - TestPublisher#complete(),
//   - TestPublisher#error(Throwable), and 
//   - TestPublisher#emit(T ...).
CLEANUP_ON_TERMINATE

// Allow the TestPublisher to ignore cancellation signals and
// continue emitting signals as if the cancellation lost race 
// against said signals.
DEFER_CANCELLATION

// Allow next calls to be made despite insufficient request, 
// without triggering an IllegalStateException.
REQUEST_OVERFLOW
```

Let's review, for example, `CLEANUP_ON_TERMINATE`, which allows a `TestPublisher` to emit signals like `complete` or `error` many times in a row.

The normal behavior for the publisher is to ignore multiple termination signals. Consider the following test:
```java
TestPublisher<Integer> testPublisher = 
                TestPublisher.create();

StepVerifier.create(testPublisher.mono())
    .then(() -> {
        testPublisher.next(1);
    })
    .expectNext(1)
    .then(testPublisher::complete)
    .then(testPublisher::complete)
    .verifyComplete();
```

There are two calls to `testPublisher.complete()`, but the second one is ignored and the test passes. 

However, if create a non-compliant `TestPublisher` passing the value `TestPublisher.Violation.CLEANUP_ON_TERMINATE`, without any other change: 
```java
TestPublisher<Integer> testPublisher = 
    TestPublisher.createNoncompliant(
        TestPublisher.Violation.CLEANUP_ON_TERMINATE
    );

StepVerifier.create(testPublisher.mono())
    .then(() -> {
        testPublisher.next(1);
    })
    .expectNext(1)
    .then(testPublisher::complete)
    .then(testPublisher::complete)
    .verifyComplete();
```

This time, the test will fail:
```
java.lang.AssertionError: expectation failed (did not expect: onComplete())
	at reactor.test.MessageFormatter.assertionError(MessageFormatter.java:115)
	at reactor.test.MessageFormatter.failPrefix(MessageFormatter.java:104)
	at reactor.test.MessageFormatter.fail(MessageFormatter.java:73)
	at reactor.test.DefaultStepVerifierBuilder$DefaultVerifySubscriber.setFailure(DefaultStepVerifierBuilder.java:1338)
	at reactor.test.DefaultStepVerifierBuilder$DefaultVerifySubscriber.onExpectation(DefaultStepVerifierBuilder.java:1440)
	at reactor.test.DefaultStepVerifierBuilder$DefaultVerifySubscriber.onComplete(DefaultStepVerifierBuilder.java:1117)
	at reactor.test.publisher.DefaultTestPublisher$TestPublisherSubscription.onComplete(DefaultTestPublisher.java:249)
	at reactor.test.publisher.DefaultTestPublisher.complete(DefaultTestPublisher.java:433)
	at reactor.test.publisher.DefaultTestPublisher.complete(DefaultTestPublisher.java:41)
    ...
```

Now that the second completion signal is not ignored, it causes an `AssertionError` because `StepVerifier` doesn't expect it.

This can be helpful to test that custom operators comply with the specification, for example.
