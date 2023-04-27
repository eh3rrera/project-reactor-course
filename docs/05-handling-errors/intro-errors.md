---
layout: default
title: Introduction to Errors in Reactor
parent: Handling Errors
nav_order: 1
---

# Introduction to Errors in Reactor
---

There are many types of errors, but for this module, we're going to define as an error all **unexpected** behaviors that can lead to an exception, so we can use the terms error and exception interchangeably.

![Unexpected](images/97.png)

In Java, when an exception happens, you can use a `try-catch` statement to catch the exception, try to fix the error in some way, and pretend nothing had happened:
```java
int val = 0;
try {
    val = Integer.parseInt("a");
} catch(NumberFormatException e) {
    logger.error(e.getMessage(), e);
    // Set a default value
    val = 1;
}
// Keep working with val
```

But in Reactor, when an exception happens, even though you can simulate the functionality of "catching an exception", the reactive sequence stops and the error is propagated down the chain of operators.

In other words, an error in Reactor is a **terminal event**.

![Stopped](images/98.png)

Consider this example:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .map(i -> i*-1)
    .subscribe(System.out::println);
```

Each element (`i`) is divided by `i-3`, so in the case of `3`, it will be divided by zero, causing an exception.

Here's part of the output of the program:
```
0
2
[ERROR] (main) Operator called default onErrorDropped - reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.ArithmeticException: / by zero
reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.ArithmeticException: / by zero
Caused by: java.lang.ArithmeticException: / by zero
	at net.eherrera.reactor.m5.Test_01_IntroErrors.lambda$example_01_Exception$0(Test_01_IntroErrors.java:14)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onNext(FluxMapFuseable.java:113)
	at reactor.core.publisher.FluxArray$ArraySubscription.fastPath(FluxArray.java:172)
	at reactor.core.publisher.FluxArray$ArraySubscription.request(FluxArray.java:97)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:171)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:171)
	at reactor.core.publisher.LambdaSubscriber.onSubscribe(LambdaSubscriber.java:119)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:96)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:96)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:53)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:59)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8469)
	at reactor.core.publisher.Flux.subscribeWith(Flux.java:8642)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8439)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8363)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8306)
	at net.eherrera.reactor.m5.Test_01_IntroErrors.example_01_Empty(Test_01_IntroErrors.java:16)
	...
```

As you can see, the results from elements `1` and `2` were printed but the rest of the sequence was stopped by an `ArithmeticException`.

In my case, the instruction in line `14` (`map(i -> i/(i-3))`) is the one that throws the exception:
```
Caused by: java.lang.ArithmeticException: / by zero
	at net.eherrera.reactor.m5.Test_01_IntroErrors.lambda$example_01_Exception$0(Test_01_IntroErrors.java:14)
```

And just like in Java, you can throw an exception after, for example, checking for a particular condition:
```java
if(list.isEmpty()) {
    throw new 
        RuntimeException("List must not be empty");
}
```

In Reactor, `Mono` and `Flux` have an `error` method that creates a sequence that terminates with the either eagerly or lazily specified error:
```java
// For Mono
Mono<T> error(Throwable error)
Mono<T> error(
    Supplier<? extends Throwable> errorSupplier
)

// For Flux
Flux<T> error(Throwable error)
Flux<O> error(
    Throwable throwable, boolean whenRequested
)
Flux<T> error(
    Supplier<? extends Throwable> errorSupplier
)
```

In all cases, the sequence terminates immediately after being subscribed to, except for the sequence returned by the `error` method from `Flux` that takes a `boolean` parameter, which if `true`, will terminate with an error on the first request (when calling `request()`) instead of on subscription (when calling `subscribe()`). 

We can combine this method with `switchIfEmpty` to create a reactive version of the imperative `if` example above:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .filter(i -> i > 10)
    .switchIfEmpty(
        Flux.error(
            new RuntimeException("List must not be empty")
        )
    )
    .subscribe(System.out::println);
```

Here's the result:
```
[ERROR] (main) Operator called default onErrorDropped - reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.RuntimeException: List must not be empty
reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.RuntimeException: List must not be empty
Caused by: java.lang.RuntimeException: List must not be empty
	at net.eherrera.reactor.m5.Test_01_IntroErrors.example_02_Error(Test_01_IntroErrors.java:24)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	...
```

It's not much different from the result of the example that throws an `ArithmeticException`.

However, notice this message about an error callback not implemented:
```
Operator called default onErrorDropped - reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.RuntimeException...
```

This error callback can be implemented in more than one way. For example, the `subscribe` method has versions that take a `Consumer<? super Throwable>` to handle errors:
```java
Disposable subscribe(
    Consumer<? super T> consumer,
    Consumer<? super Throwable> errorConsumer
)
```

The following code is the equivalent of caching the exception and print it:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .map(i -> i*-1)
    .subscribe(
        System.out::println,
        System.out::println
    );
```

Here's the result:
```
0
2
java.lang.ArithmeticException: / by zero
```

The sequence is still stopped because of the exception, but this time, it's caught in the error consumer of the `subscribe` method, and no stack trace is printed. It'd be the equivalent of this:
```java
try {
    i = i/(i-3);
    i = i*-1;
} catch(ArithmeticException e) {
    System.out.println(e);
}
```

However, this is not the most helpful way of dealing with an exception.

For that reason, Reactor provides a series of operators to return a default value or re-throw the exception, for example.
 
Let's review them next.

