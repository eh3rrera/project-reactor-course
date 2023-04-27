---
layout: default
title: Going Back to Synchronous Types
parent: Working with Blocking Calls
nav_order: 1
---

# Going Back to Synchronous Types
---

If you're working with `Mono` or `Flux` but you need to return, at the end of a chain of operators, a synchronous type like a `List` or an `Integer`, you can use one of the `block*` methods that Reactor provides.

These methods subscribe to a `Publisher` and, as the name implies, **block** the execution of the program to get the result.

![Block](images/103.png)

Here are the methods for `Mono`.

`block` subscribes to the `Mono` and blocks indefinitely until the element is received, returning that element:
```java 
T block()
```

Another version subscribes to the `Mono` and blocks until the element is received or the timeout expires, returning the element:
```java 
T block(Duration timeout)
```

`blockOptional` subscribes to the `Mono` and blocks indefinitely until the element is received, or the `Mono` completes empty, returning either the element wrapped in an `Optional` or an empty `Optional`:
```java 
Optional<T> blockOptional()
```

Another version subscribes to the `Mono` and blocks until the element is received, the `Mono` completes empty, or the timeout expires, returning either the element wrapped in an `Optional` or an empty `Optional`:
```java 
Optional<T> blockOptional(Duration timeout)
```

And here are the methods for `Flux`.

`blockFirst` subscribes to the `Flux` and blocks indefinitely until the first value is sent or the `Flux` completes, returning the first value or `null`:
```java
T blockFirst()
```
    
Another version subscribes to the `Flux` and blocks until the first value is sent, the `Flux` completes, or the timeout expires, returning the first value or `null`:
```java
T blockFirst(Duration timeout)
```
    
`blockLast` subscribes to the `Flux` and blocks indefinitely until the last value is sent or the `Flux` completes, returning the last value or `null`:
```java
T blockLast()
```
    
While another version subscribes to the `Flux` and blocks until the upstream signals its last value, completes or a timeout expires:
```java
T blockLast(Duration timeout)
```

We have four options for each `Publisher`. Two of them take as an argument a timeout as a [Duration](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Duration.html) object. If the timeout expires, a `RuntimeException` is thrown.

Here's a simple example that returns the `Integer` value inside of a `Mono` (remember, sometimes it's useful to think of `Mono` or `Flux` as containers):
```java
Mono<Integer> myMono = Mono.just(1);
Integer valueMono = myMono.block();
System.out.println(valueMono);
```

Notice there's no `subscribe` method.

This is the result:
```
1
```

For the versions that take a timeout, the following example forces a timeout expiration by delaying the publishing of elements with the `delayElements` operator:
```java
Integer valueFlux = 
    Flux.just(1, 2, 3)
        .delayElements(Duration.ofSeconds(1))
        .blockLast(Duration.ofMillis(1));
```

In this case, this piece of code throws an exception:
```
java.lang.IllegalStateException: Timeout on blocking read for 1000000 NANOSECONDS

	at reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:123)
	at reactor.core.publisher.Flux.blockLast(Flux.java:2669)
	at net.eherrera.reactor.m8.Test_01_BlockMethods.example_02_BlockWithDuration(Test_01_BlockMethods.java:24)
	...
```

These methods work in a synchronous way, not in an asynchronous way. Blocking **defeats** the purpose of reactive programming, so you'll rarely have to use these methods. Besides, blocking the execution in a reactive web server that works with a limited amount of threads, can bring it down easily.

The only two uses cases I can think of for these `block*` methods are:
- When you need the result of a sequence to call a third-party library that doesn't have a reactive version.
- In unit tests, if you're not using a library like `StepVerifier`, and you need to assert the result of a sequence.

However, you must know that in the case of `Flux`, the following methods block the execution every time you get an element from the transformed `Iterable` or `Stream`:
```java
// Transform this Flux into a lazy Iterable,
// blocking on Iterator.next() calls
Iterable<T> toIterable()
    
// Transform this Flux into a lazy Iterable,
// blocking on Iterator.next() calls
Iterable<T> toIterable(int batchSize)

// Transform this Flux into a lazy Iterable,
// blocking on Iterator.next() calls
Iterable<T> toIterable(
    int batchSize, 
    Supplier<Queue<T>> queueProvider
)
    
// Transform this Flux into a lazy Stream,
// blocking for each source onNext call
Stream<T> toStream()
    
// Transform this Flux into a lazy Stream,
// blocking for each source onNext call
Stream<T> toStream(int batchSize)
```

Of course, this shouldn't be a surprise, since `Iterable` and `Stream` are not reactive.

On the other hand, `Mono` can only be transformed into a `CompletableFuture`:
```java
// To transform the Mono into a CompletableFuture,
// completing on onNext or onComplete,
// and failing on onError.
CompletableFuture<T> toFuture()
```

The result (or `null`) is passed asynchronously to `CompletableFuture`'s `complete` method, but to get it, you have to call the `get()` method.

Also, here's a note from the [documentation](https://projectreactor.io/docs/core/release/reference/#which.blocking) to keep in mind:
> All of these methods except Mono#toFuture will throw an UnsupportedOperatorException if called from within a Scheduler marked as "non-blocking only" (by default parallel() and single()).

But remember, neven block the execution.
