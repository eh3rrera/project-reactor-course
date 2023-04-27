---
layout: default
title: Error-Related Operators
parent: Handling Errors
nav_order: 2
---

# Error-Related Operators
---

In this lesson, we're going to review the following operators:
- [doOnError](#doonerror)
- [onErrorReturn](#onerrorreturn)
- [onErrorResume](#onerrorresume)
- [onErrorMap](#onerrormap)
- [onErrorContinue](#onerrorcontinue)
- [onErrorStop](#onerrorstop)

## doOnError
If you remember from the previous module, `doOnError` is one of the life cycle hooks Reactor provides. 

There are three versions of `doOnError` for `Mono<T>`. For all of them, the `Consumer` is executed first, and then the `onError` signal is propagated.

The first one adds behavior when the `Mono` completes with an error matching the given exception type:
```java
<E extends Throwable> Mono<T> doOnError(
    Class<E> exceptionType, 
    Consumer<? super E> onError
)
```

The second one adds behavior triggered when the `Mono` completes with an error:
```java
Mono<T> doOnError(
    Consumer<? super Throwable> onError
)
```

And the third one adds behavior when the `Mono` completes with an error matching the given predicate:
```java
Mono<T> doOnError(
    Predicate<? super Throwable> predicate, 
    Consumer<? super Throwable> onError
)
```

`Flux` also has three versions. Once again, the `Consumer` is executed first, and then the `onError` signal is propagated.

The first one adds behavior when the `Flux` completes with an error matching the given exception type:
```java
<E extends Throwable> Flux<T> doOnError(
    Class<E> exceptionType, Consumer<? super E> onError
)
```

The second one adds behavior triggered when the `Flux` completes with an error:
```java
Flux<T> doOnError(
    Consumer<? super Throwable> onError
)
```

And the third one adds behavior when the `Flux` completes with an error matching the given predicate:
```java
Flux<T> doOnError(
    Predicate<? super Throwable> predicate, 
    Consumer<? super Throwable> onError
)
```

Remember that lifecycle hooks add behavior at certain points of the execution of a reactive sequence. With `doOnError`, you can catch the exception, do something with it, and then let it propagated downstream. 

In other words, using `doOnError` like this:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .doOnError(ArithmeticException.class,
            e -> 
               System.out.println(
                    "ArithmeticException: " + e.getMessage()
            )
    )
    .subscribe(System.out::println);
```

Would be the equivalent of this:
```java
try {
    i = i/(i-3);
} catch(ArithmeticException e) {
    System.out.println(
        "ArithmeticException: " + e.getMessage()
    );
    throw e;
}
```

This is the result of running the example:
```
0
-2
ArithmeticException: / by zero
[ERROR] (main) Operator called default onErrorDropped - reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.ArithmeticException: / by zero
reactor.core.Exceptions$ErrorCallbackNotImplemented: java.lang.ArithmeticException: / by zero
Caused by: java.lang.ArithmeticException: / by zero
	at net.eherrera.reactor.m5.Test_02_ErrorOperators.lambda$example_01_doOnError$0(Test_02_ErrorOperators.java:14)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onNext(FluxMapFuseable.java:113)
	at reactor.core.publisher.FluxArray$ArraySubscription.fastPath(FluxArray.java:172)
	at reactor.core.publisher.FluxArray$ArraySubscription.request(FluxArray.java:97)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.request(FluxMapFuseable.java:171)
	at reactor.core.publisher.FluxPeekFuseable$PeekFuseableSubscriber.request(FluxPeekFuseable.java:144)
	at reactor.core.publisher.LambdaSubscriber.onSubscribe(LambdaSubscriber.java:119)
	at reactor.core.publisher.FluxPeekFuseable$PeekFuseableSubscriber.onSubscribe(FluxPeekFuseable.java:178)
	at reactor.core.publisher.FluxMapFuseable$MapFuseableSubscriber.onSubscribe(FluxMapFuseable.java:96)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:53)
	at reactor.core.publisher.FluxArray.subscribe(FluxArray.java:59)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8466)
	at reactor.core.publisher.Flux.subscribeWith(Flux.java:8639)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8436)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8360)
	at reactor.core.publisher.Flux.subscribe(Flux.java:8303)
	...
```

As you can see, the error is propagated to the `subscribe` method. We can add a consumer of the error to avoid the stack trace:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .doOnError(ArithmeticException.class,
            e -> 
               System.out.println(
                    "doOnError: " + e.getMessage()
            )
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

This is the result:
```
0
-2
doOnError: / by zero
java.lang.ArithmeticException: / by zero
```

By the way, another lifecycle method that can be useful when handling exceptions is `doFinally`:
```java
// For Mono
Mono<T> doFinally(
    Consumer<SignalType> onFinally
)
    
// For Flux
Flux<T> doFinally(Consumer<SignalType> onFinally)
```

Remember, this method adds behavior after the sequence terminates for any reason, including cancellation and errors. This way, it can work similarly to a `finally` clause in a `try-catch` statement.

Consider the following example:
```java
Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
integerFlux
    .map(i -> i/(i-3))
    .doOnError(ArithmeticException.class,
            e -> System.out.println(
                    "doOnError: " + e.getMessage()
            )
    )
    .doFinally(signalType -> 
                    System.out.println("doFinally: " + signalType)
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

It's the equivalent of the following `try-catch-finally` statement:
```java
try {
    i = i/(i-3);
} catch(ArithmeticException e) {
    System.out.println(
        "ArithmeticException: " + e.getMessage()
    );
    throw e;
} finally {
    System.out.println("doFinally: ...");
}
```

And this will be the result:
```
0
-2
doOnError: / by zero
java.lang.ArithmeticException: / by zero
doFinally: onError
```

Notice the print statement from `doFinally` was the last one to be executed and the signal type is `onError`.

## onErrorReturn
Now, what if we want to return a default or fallback value when catching an exception?

We can do this with the operator `onErrorReturn`.

There are three versions of this operator for `Mono<T>`.

The first one emits a captured fallback value when an error is observed on the `Mono`.
```java
Mono<T> onErrorReturn(T fallback)
```

The second one emits a captured fallback value when an error of the specified type is observed on the `Mono`:
```java
<E extends Throwable> Mono<T> onErrorReturn(
    Class<E> type, 
    T fallbackValue
)
```

And the third one emits a captured fallback value when an error matching the given predicate is observed on the `Mono`:
```java
Mono<T> onErrorReturn(
    Predicate<? super Throwable> predicate, 
    T fallbackValue
)
```

`Flux` also has three versions of this operator.

The first one emits a captured fallback value when an error is observed on the `Flux`:
```java
Flux<T> onErrorReturn(T fallbackValue)
```

The second one emits a captured fallback value when an error of the specified type is observed on the `Flux`:
```java
<E extends Throwable> Flux<T> onErrorReturn(
    Class<E> type, 
    T fallbackValue
)
```

And the third one emits a captured fallback value when an error matching the given predicate is observed on the `Flux`:
```java
Flux<T> onErrorReturn(
    Predicate<? super Throwable> predicate, 
    T fallbackValue
)
```

For example, when dividing by zero, instead of letting the `ArithmeticException` propagate, we can provide a fallback value, let's say `0`:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorReturn(
        ArithmeticException.class, 
        0
    )
    .subscribe(
        System.out::println,
        System.out::println
    );
```

This is the result:
```
0
-2
0
```

Notice that after the error, the sequence stopped. Remember, all errors in Reactor are terminal, `onErrorReturn` only provides a fallback value.

Finally, you can also apply a `Predicate` on the exception to decide whether or not to return a fallback value.

For example, we can test if the exception message contains a particular string:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);
integerFlux
    .map(i -> i/(i-3))
    .onErrorReturn(
        ArithmeticException.class, 
        0
    )
    .subscribe(
        System.out::println,
        System.out::println
    );
```

In this case, since the exception message doesn't contain the string `3`, the exception is caught by the consumer of the `subscription` method:
```
0
-2
java.lang.ArithmeticException: / by zero
```

## onErrorResume
Instead of providing a simple value, you might want to provide a fallback sequence. You can do this with `onErrorResume`.

There are three versions of this operator for `Mono<T>`. All of them use a function to choose the fallback depending on the error.

The first one subscribes to a fallback publisher when any error occurs:
```java
Mono<T> onErrorResume(
    Function<? super Throwable,? extends Mono<? extends T>> fallback
)
```

The second one subscribes to a fallback publisher when an error matching the given type occurs:
```java
<E extends Throwable> Mono<T> onErrorResume(
    Class<E> type, 
    Function<? super E, ? extends Mono<? extends T>> fallback
)
```
    
The third one subscribes to a fallback publisher when an error matching a given predicate occurs:
```java
Mono<T> onErrorResume(
    Predicate<? super Throwable> predicate, 
    Function<? super Throwable, ? extends Mono<? extends T>> fallback
)
```

`Flux` also has three versions for this operator that use a function to choose the fallback depending on the error.

The first one subscribes to a returned fallback publisher when any error occurs:
```java
Flux<T> onErrorResume(
    Function<? super Throwable, ? extends Publisher<? extends T>> fallback
)
```

The second one subscribes to a fallback publisher when an error matching the given type occurs:
```java
<E extends Throwable> Flux<T> onErrorResume(
    Class<E> type, 
    Function<? super E, ? extends Publisher<? extends T>> fallback
)
```

And the third one subscribes to a fallback publisher when an error matching a given predicate occurs:
```java
Flux<T> onErrorResume(
    Predicate<? super Throwable> predicate, 
    Function<? super Throwable, ? extends Publisher<? extends T>> fallback
)
```

Just like `onErrorReturn`, `onErrorResume` has versions that take the class of the exception that you want to handle or a `Predicate` to conditionally handle an exception.

However, unlike `onErrorReturn`, you can pass to `onErrorResume` a function that takes the exception thrown and returns a fallback publisher. This way, the original sequence will be canceled, and `onErrorResume` will subscribe to the fallback publisher to keep emitting elements to the subscriber.

For example, as we know that the value `3` prevents the emission of the values `4` and `5`, we can define a fallback publisher with these elements:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorResume(e -> Flux.just(4, 5))
    .subscribe(System.out::println,
            System.out::println
    );
```

This will be the result:
```
0
-2
4
5
```

However, notice that the `map` operator that divides the value by `i-3` is not executed against the fallback sequence, this is emitted just as it is. That's something to take into account.

All right, but you might be wondering, what if we place `onErrorResume` before the `map` operator? Something like this:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .onErrorResume(e -> Flux.just(4, 5))
    .map(i -> i/(i-3))
    .subscribe(System.out::println,
            System.out::println
    );
```

What do you think it will happen?

Will the elements from the fallback publisher go through the `map` operator?

If you run the above example, this will be the result:
```
0
-2
java.lang.ArithmeticException: / by zero
```

As you can see, the exception thrown by the `map` operator was not caught, so we can infer that `onErrorResume` can only catch the exceptions thrown upstream of it.

`onErrorResume` is a very useful operator because given the exception thrown, you can dynamically generate a fallback `Publisher` or value wrapped in `Publisher`.

Taking this into account, and with the help of the `error` method from `Mono` or `Flux`, you can replicate the behavior of catching an exception, wrapping it into a different exception, and re-throw it.

For example, the following imperative code:
```java
try {
  i = i/(i-3);
} catch (ArithmeticException e) {
  throw new RuntimeException(
    "Unexpected exception", e
  );
}
```

Would be the equivalent of the following reactive code:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorResume(e -> 
                     Flux.error(
                        new RuntimeException("Unexpected exception", e)
                     )
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

And this would be the result:
```
0
-2
java.lang.RuntimeException: Unexpected exception
```

## onErrorMap
Another option for replicating the functionality of "catching, wrapping into a different exception, and re-throwing it" is the `onErrorMap` operator.

There are three versions of this operator for `Mono<T>`.

The first version transforms any error emitted by the `Mono` by synchronously applying a function to it:
```java
Mono<T> onErrorMap(
    Function<? super Throwable, ? extends Throwable> mapper
)
```

The second version transforms an error emitted by the `Mono` by synchronously applying a function to it if the error matches the given type:
```java
<E extends Throwable> Mono<T> onErrorMap(
    Class<E> type, 
    Function<? super E,? extends Throwable> mapper
)
```

And the third version transforms an error emitted by the `Mono` by synchronously applying a function to it if the error matches the given predicate:
```java
Mono<T> onErrorMap(
    Predicate<? super Throwable> predicate, 
    Function<? super Throwable, ? extends Throwable> mapper
)
```

`Flux` also provides three versions. The first one transforms any error emitted by the `Flux` by synchronously applying a function to it:
```java
Flux<T> onErrorMap(
    Function<? super Throwable, ? extends Throwable> mapper
)
```

The second one transforms an error emitted by the `Flux` by synchronously applying a function to it if the error matches the given type:
```java
<E extends Throwable> Flux<T> onErrorMap(
    Class<E> type, 
    Function<? super E,? extends Throwable> mapper
)
```

And the third one transforms an error emitted by the `Flux` by synchronously applying a function to it if the error matches the given predicate:
```java
Flux<T> onErrorMap(
    Predicate<? super Throwable> predicate, 
    Function<? super Throwable,? extends Throwable> mapper
)
```

For example, the previous imperative code:
```java
try {
  i = i/(i-3);
} catch (ArithmeticException e) {
  throw new RuntimeException(
    "Unexpected exception", e
  );
}
```

Would look like this using `onErrorMap`:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorMap(e -> new RuntimeException(
                        "Unexpected exception", e)
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

The result is the same as before:
```
0
-2
java.lang.RuntimeException: Unexpected exception
```

The only difference with `onErrorResume` is that we didn't have to use the `error` method to wrap the exception in a `Publisher`, the function `onErrorMap` takes returns the exception directly.

## onErrorContinue
So all errors are terminal, but what if we need to keep processing the stream after an exception is thrown? For this, we can use the `onErrorContinue` operator.

There are three versions of this operator for `Mono<T>`. All of them let compatible operators upstream recover from errors by dropping the incriminating element from the sequence and continuing with subsequent elements:
```java
Mono<T> onErrorContinue(
    BiConsumer<Throwable,Object> errorConsumer
)

// Only errors matching the specified type 
// are recovered from.
<E extends Throwable> Mono<T> onErrorContinue(
    Class<E> type, 
    BiConsumer<Throwable,Object> errorConsumer
)

// Takes a Predicate used to filter which errors 
// should be resumed from.
<E extends Throwable> Mono<T> onErrorContinue(
    Predicate<E> errorPredicate, 
    BiConsumer<Throwable,Object> errorConsumer
)
```

And here are the versions for `Flux<T>`:
```java
Flux<T> onErrorContinue(
    BiConsumer<Throwable,Object> errorConsumer
)

// Only errors matching the specified type 
// are recovered from.
<E extends Throwable> Flux<T> onErrorContinue(
    Class<E> type, 
    BiConsumer<Throwable,Object> errorConsumer
)

// Takes a Predicate used to filter which errors 
// should be resumed from.
<E extends Throwable> Flux<T> onErrorContinue(
    Predicate<E> errorPredicate, 
    BiConsumer<Throwable,Object> errorConsumer
)
```

As you can see from the comments, `onErrorContinue` allows **upstream** operators (this is a key concept) to drop the element that caused the exception and continue processing the subsequent elements. 

The exception and the value that caused that exception are passed to the `BiConsumer` all versions take.

Here's an example:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This is the result:
```
0
-2
The value 3 caused the exception: java.lang.ArithmeticException: / by zero
4
2
```

As you can see, after the error, the sequence continued processing the elements `4` and `5`.

However, throwing an exception inside the `BiConsumer` will propagate it downstream in place of the original one.

Here's an example:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .map(i -> i/(i-3))
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
        throw new RuntimeException(e);
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This is the result:
```
0
-2
The value 3 caused the exception: java.lang.ArithmeticException: / by zero
java.lang.RuntimeException: java.lang.ArithmeticException: / by zero
```

Despite all this, the use of `onErrorContinue` is not recommended.

Consider this example:
```java
Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
integerFlux
    .map(i -> i/(i-3))
    .onErrorResume(e -> Mono.just(99))
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
        throw new RuntimeException(e);
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

What do you think will happen?

Will the error be caught by `onErrorResume`?

By `onErrorContinue`?

Both?

This is the result:
```java
0
-2
The value 3 caused the exception: java.lang.ArithmeticException: / by zero
99
```

Well, the error was caught by both, `onErrorResume` and `onErrorContinue`. However, `onErrorContinue` caught the error first and didn't continue processing elements `4` and `5`, the fallback `Mono` was emitted instead.

On the other hand, if we switch the order of `onErrorResume` and `onErrorContinue`:
```java
Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
integerFlux
    .map(i -> i/(i-3))
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
    })
    .onErrorResume(e -> Mono.just(99))
    .subscribe(System.out::println,
            System.out::println
    );
```

This will be the result:
```
0
-2
The value 3 caused the exception: java.lang.ArithmeticException: / by zero
4
2
```

`onErrorResume` will not work, it will do nothing.

So you have to be careful when mixing `onErrorResume` and `onErrorContinue`. In particular, because sometimes a caller might add `onErrorContinue` to the chain of operators, unexpectedly changing the behavior.

According to the [documentation](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorContinue-java.util.function.BiConsumer-):
> Note that onErrorContinue() is a specialist operator that can make the behaviour of your reactive chain unclear. It operates on upstream, not downstream operators, it requires specific operator support to work, and the scope can easily propagate upstream into library code that didn't anticipate it (resulting in unintended behavior.)

Next, the documentation goes on to provide a solution that will avoid leaking upstream:
> In most cases, you should instead handle the error inside the specific function which may cause it. Specifically, on each inner publisher you can use doOnError to log the error, and onErrorResume(e -> Mono.empty()) to drop erroneous elements.

Here's an example that encapsulates the code that throws an exception in a `flatMap` operator:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .flatMap(val ->
        Mono.just(val)
                .map(i -> i/(i-3))
                .doOnError(
                    e -> System.out.println(
                            "Inside exception: " + e
                    )
                )
                .onErrorResume(e -> Mono.empty())
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

This is the result:
```
0
-2
Inside exception: java.lang.ArithmeticException: / by zero
4
2
```

It has the same effect as using `onErrorContinue`, but you won't have unexpected side effects if this operator appears before `flatMap`:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
    })
    .flatMap(val ->
            Mono.just(val)
                    .map(i -> i/(i-3))
                    .doOnError(
                        e -> System.out.println(
                                "Inside exception: " + e
                        )
                    )
                    .onErrorResume(e -> Mono.empty())
    )
    .subscribe(System.out::println,
            System.out::println
    );
```

For the above example, `onErrorContinue` won't have any effect:
```
0
-2
Inside exception: java.lang.ArithmeticException: / by zero
4
2
```

## onErrorStop
If we modify the previous example to place `onErrorContinue` after the `flatMap` operator:
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .flatMap(val ->
            Mono.just(val)
                    .map(i -> i/(i-3))
                    .doOnError(e -> 
                                System.out.println(
                                    "Inside exception: " + e
                                )
                    )
                    .onErrorResume(e -> Mono.empty())
    )
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This time, `onErrorContinue` will be executed:
```
0
-2
The value 3 caused the exception: java.lang.ArithmeticException: / by zero
4
2
```

But if you don't want this behavior, you can use `onErrorStop`:
```java
// For Mono
Mono<T> onErrorStop()

// For Flux
Flux<T> onErrorStop()
```

If an `onErrorContinue(BiConsumer)` variant has been used downstream, `onErrorStop` reverts to the default mode where errors are terminal events upstream. In other words, after `onErrorStop`, `onErrorContinue` will have no effect.

This way, if we add to the above example the `onErrorStop` operator (after `onErrorStop` and before `onErrorContinue`):
```java
Flux<Integer> integerFlux = 
    Flux.just(1, 2, 3, 4, 5);

integerFlux
    .flatMap(val ->
            Mono.just(val)
                    .map(i -> i/(i-3))
                    .doOnError(e -> 
                                    System.out.println(
                                        "Inside exception: " + e
                                    )
                    )
                    .onErrorResume(e -> Mono.empty())
                    .onErrorStop()
    )
    .onErrorContinue((e, i) -> {
        System.out.format(
            "The value %d caused the exception: %s\n", i, e
        );
    })
    .subscribe(System.out::println,
            System.out::println
    );
```

This time, `onErrorContinue` will have no effect:
```
0
-2
Inside exception: java.lang.ArithmeticException: / by zero
4
2
```
