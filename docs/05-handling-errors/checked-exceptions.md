---
layout: default
title: Handling Checked Exceptions
parent: Handling Errors
nav_order: 3
---

# Handling Checked Exceptions
---

We've been working with unchecked exceptions, exceptions that inherit from `RuntimeException` and that don't need to be caught with a `try-catch` statement.

But sometimes, you'll have to work with checked exceptions, exceptions that inherit directly from `Exception` and that need to be caught with a `try-catch` statement.

![cauhgt](images/99.png)

If you're using the `error` method from `Mono` or `Flux` you don't need to change anything, this method takes a `Throwable`, so any kind of exception should work:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);
integerFlux
    .filter(i -> i > 10)
    .switchIfEmpty(
        Flux.error(
            new Exception("List must not be empty")
        )
    )
    .subscribe(System.out::println);
```

If you run the program, you will see that the result is the same as if you were using an unchecked exception:
```
[ERROR] (main) Operator called default onErrorDropped - reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.Exception: List must not be empty
reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.Exception: List must not be empty
Caused by: java.lang.Exception: List must not be empty
	at net.eherrera.reactor.m5.Test_03_CheckedExceptions.example_01_CheckedException(Test_03_CheckedExceptions.java:15)
	...
```

However, if you use a method that throws a checked exception inside an operator:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> getValue(i))  // This won't compile
    .subscribe(System.out::println);
// ...
private int getValue(int i) throws Exception {
    if(i < 0) {
        throw new Exception(
            "The input value cannot be zero"
        );
    }
    return i * 10;
}
```

The code will not compile unless you handle the checked exception in some way.

Actually, there are three ways:
1. Wrap the exception in a `Mono` or `Flux` with the `error` method, as I previously showed you.
2. Catch the exception and recover from it (probably using a default value) so the flow of the program can continue normally.
3. Catch the exception, wrap it into an unchecked exception, and re-throw it.

I already showed you the first method, however, in the case of the example above, the method `getValue` doesn't return a `Mono` or `Flux` so we can't use it.

Another option is using a `try-catch` statement inside the lambda expression of the `map` operator. Something like this:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, -44, 5);

integerFlux
    //.map(i -> getValue(i)) // This won't compile
    .map(i -> {
        try {
            return getValue(i);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    })
    .subscribe(System.out::println);
```

In this case, we're catching the exception, printing its stack trace, and retuning a default value, effectively swallowing the exception so the program can continue its execution.

This is the result:
```
10
20
30
java.lang.Exception: The input value cannot be zero
	at net.eherrera.reactor.m5.Test_03_CheckedExceptions.getValue(Test_03_CheckedExceptions.java:74)
	at net.eherrera.reactor.m5.Test_03_CheckedExceptions.lambda$example_02_CheckedException$1(Test_03_CheckedExceptions.java:29)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onNext(FluxMapFuseable.java:113)
	at reactor.core.publisher.FluxArray$ArraySubscription.fastPath(FluxArray.java:172)
	at reactor.core.publisher.FluxArray$ArraySubscription.request(FluxArray.java:97)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:171)
	at reactor.core.publisher.LambdaSubscriber.onSubscribe(LambdaSubscriber.java:119)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:96)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:53)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:59)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8466)
	at reactor.core.publisher.Flux.subscribeWith(Flux.java:8639)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8436)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8360)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8303)
	...
0
50
```

The third option is about converting the checked exception into an unchecked exception so we can handle it later, using an operator such as `onErrorReturn`, or at the time of subscription, for example.

For this, Reactor provides the [Exceptions](https://projectreactor.io/docs/core/release/api/reactor/core/Exceptions.html) utility class. In particular, three methods can help us deal with checked exceptions:
```java
// Prepare an unchecked RuntimeException 
// that will bubble upstream if thrown by an operator
static RuntimeException bubble(Throwable t)

// Prepare an unchecked RuntimeException 
// that should be propagated downstream 
// through Subscriber.onError(Throwable)
static RuntimeException propagate(Throwable t)

// Unwrap a particular Throwable only if
// it is wrapped via bubble or propagate.
static Throwable unwrap(Throwable t)
```

If you just want to wrap the checked exception and rethrow it, you can use the `bubble` method this way:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, -4, 5);

integerFlux
    //.map(i -> getValue(i))  // This won't compile
    .map(i -> {
        try {
            return getValue(i);
        } catch (Exception e) {
            throw Exceptions.bubble(e);
        }
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This is the result:
```
10
20
30

reactor.core.Exceptions$BubblingException: java.lang.Exception: The input value cannot be zero
	at reactor.core.Exceptions.bubble(Exceptions.java:172)
	at net.eherrera.reactor.m5.Test_03_CheckedExceptions.lambda$example_03_bubble$2(Test_03_CheckedExceptions.java:47)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onNext(FluxMapFuseable.java:113)
	at reactor.core.publisher.FluxArray$ArraySubscription.fastPath(FluxArray.java:172)
	at reactor.core.publisher.FluxArray$ArraySubscription.request(FluxArray.java:97)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:171)
	at reactor.core.publisher.LambdaSubscriber.onSubscribe(LambdaSubscriber.java:119)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:96)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:53)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:59)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8466)
	at reactor.core.publisher.Flux.subscribeWith(Flux.java:8639)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8436)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8360)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8330)
	...
```

As you can see, even though the `subscribe` method has a consumer for the exception, the exception was not caught.

On the other hand, if we use the `propagate` method:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, -4, 5);

integerFlux
    //.map(i -> getValue(i))  // This won't compile
    .map(i -> {
        try {
            return getValue(i);
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This will be the result:
```java
10
20
30
java.lang.Exception: The input value cannot be zero
```

`Exceptions.bubble(Throwable)` wraps the given exception into a `BubblingException` (that extends from `ReactiveException`, which in turn extends from `RuntimeException`), while `Exceptions.propagate(Throwable)` wraps the exception into a `ReactiveException` if the exception is not a `RuntimeException` (if this is the case, the exception is just returned). 

In the previous example, it wasn't necessary to unwrap the exception, but if you want to do it explicitly, you can use the `unwrap` method this way:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, -4, 5);

integerFlux
    //.map(i -> getValue(i))  // This won't compile
    .map(i -> {
        try {
            return getValue(i);
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
    })
    .subscribe(System.out::println,
            e -> System.out.println(Exceptions.unwrap(e))
    );
```

But the result will be the same:
```
10
20
30
java.lang.Exception: The input value cannot be zero
```
