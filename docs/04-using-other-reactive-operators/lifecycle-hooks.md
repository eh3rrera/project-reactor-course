---
layout: default
title: Life Cycle Hooks
parent: Using Other Reactive Operators
nav_order: 7
---

# Life Cycle Hooks
---
We've been using the version of the `subscribe` method that takes a `Consumer` that is invoked for each element that a `Mono` or `Flux` emit.

This `Consumer` is invoked for every `onNext` signal.

However, the `log` operator (available for `Mono` and `Flux`) allows us to log all the Reactive Streams signals using [Logger](https://projectreactor.io/docs/core/release/api/reactor/util/Logger.html):
```java
Flux<T> log()
```

This operator has more versions.

There's one to observe Reactive Streams signals matching the passed filter options, and trace them using a specific user-provided `Logger` (instead of resolving one) at `Level.INFO` level:
```java
Flux<T> log(Logger logger)
```

Another one that also takes a `boolean` to capture the current stack to display operator class/line number (default in other versions is `false`), and a varargs `SignalType` option to filter log messages from particular signals:
```java
Flux<T> log(Logger logger, Level level, boolean showOperatorLine, SignalType... options)
```

By default, this operator will use `Level.INFO` and `java.util.logging`. However, [SLF4J](http://www.slf4j.org/) will be used if it's available.

There's also a version that takes the category to be mapped into logger configuration (e.g. `org.springframework.reactor`). If category ends with `"."` like `"reactor."`, a generated operator suffix will be added (e.g. `"reactor.Flux.Map"`):
```java
Flux<T> log(String category)
```

Another that takes the level to enforce for the sequence (only `FINEST`, `FINE`, `INFO`, `WARNING`, and `SEVERE`), and a varargs `SignalType` option to filter log messages:
```java
Flux<T> log(String category, Level level, SignalType... options)
```

And another that in addition to the category, level, and `SignalType` options, also takes a `boolean` to capture the current stack to display operator class/line number:
```java
Flux<T> log(String category, Level level, boolean showOperatorLine, SignalType... options)
``` 

Consider this example:
```java
Flux.just(1, 2, 3)
    .log()
    .subscribe(System.out::println);
``` 

It will print information about the signals `onSubscribe`, `onNext`, and `onComplete` (in addition to the values of the `Flux`):
```
[ INFO] (main) | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[ INFO] (main) | request(unbounded)
[ INFO] (main) | onNext(1)
1
[ INFO] (main) | onNext(2)
2
[ INFO] (main) | onNext(3)
3
[ INFO] (main) | onComplete()
``` 

However, if we want to *add* a customized behavior to these signals, Rector provides some hooks to do it.

First, the hooks for `Mono<T>`.

This hook adds behavior after the `Mono` terminates, either successfully or with an error:
```java
Mono<T> doAfterTerminate(Runnable afterTerminate)
```

This one adds behavior after the `Mono` terminates for any reason, including cancellation:
```java
Mono<T> doFinally(Consumer<SignalType> onFinally)
```

This one adds behavior triggered before the `Mono` is subscribed to, which should be the first event:
```java
Mono<T> doFirst(Runnable onFirst)
```

This one adds behavior triggered when the `Mono` is cancelled:
```java
Mono<T> doOnCancel(Runnable onCancel)
```

The following hook potentially modifies the behavior of the whole chain of operators upstream (before) of this one, conditionally cleaning up elements that get discarded by these operators. `discardHook` **must** be idempotent and safe to use on any instance of the desired type. Also, calls to this method are additive, and the order of invocation is the same as the order of declaration:
```java
Mono<T> doOnDiscard(Class<R> type, Consumer<? super R> discardHook)
```

This one adds behavior triggered when the `Mono` emits an item, fails with an error or completes successfully:
```java
Mono<T> doOnEach(Consumer<? super Signal<T>> signalConsumer)
```

This one adds behavior when the `Mono` completes with an error matching the given exception type (the `Consumer` is executed first, then the `onError` signal is propagated):
```java
<E extends Throwable> Mono<T> doOnError(Class<E> exceptionType, Consumer<? super E> onError)
```

The following version adds behavior triggered when the `Mono` completes with an error:
```java
Mono<T> doOnError(Consumer<? super Throwable> onError)
```

And this version adds behavior when the `Mono` completes with an error matching the given predicate:
```java
Mono<T> doOnError(Predicate<? super Throwable> predicate, Consumer<? super Throwable> onError)
```

Moving on, this hook adds behavior triggered when the `Mono` emits an item successfully:
```java
Mono<T> doOnNext(Consumer<? super T> onNext)
```

This one adds behavior triggering a `LongConsumer` when the `Mono` receives any request:
```java
Mono<T> doOnRequest(LongConsumer consumer)
```

This one adds behavior when the `Mono` is being subscribed, that is to say when a `Subscription` has been produced by the `Publisher` and is being passed to `Subscriber.onSubscribe(Subscription)`:
```java
Mono<T> doOnSubscribe(Consumer<? super Subscription> onSubscribe)
```

This one adds behavior as soon as the `Mono` can be considered to have completed successfully:
```java
Mono<T> doOnSuccess(Consumer<? super T> onSuccess)
```

And this adds behavior when the `Mono` terminates, either by completing with a value, completing empty or failing with an error:
```java
Mono<T> doOnTerminate(Runnable onTerminate)
``` 

For `Flux<T>`, this hook adds behavior after the `Flux` terminates, either successfully or with an error:
```java
Flux<T> doAfterTerminate(Runnable afterTerminate)
``` 

This one adds behavior after the `Flux` terminates for any reason, including cancellation:
```java
Flux<T> doFinally(Consumer<SignalType> onFinally)
``` 

This one adds behavior triggered before the `Flux` is subscribed to, which should be the first event:
```java
Flux<T> doFirst(Runnable onFirst)
``` 

This one adds behavior triggered when the `Flux` is cancelled:
```java
Flux<T> doOnCancel(Runnable onCancel)
``` 

This one adds behavior when the `Flux` completes successfully:
```java
Flux<T> doOnComplete(Runnable onComplete)
``` 

The following hook potentially modifies the behavior of the whole chain of operators upstream (before) of this one, conditionally cleaning up elements that get discarded by these operators. `discardHook` **must** be idempotent and safe to use on any instance of the desired type, and calls to this method are additive, and the order of invocation is the same as the order of declaration:
```java
Flux<T> doOnDiscard(Class<R> type, Consumer<? super R> discardHook)
``` 

This one adds behavior when the `Flux` emits an item, fails with an error or completes successfully:
```java
Flux<T> doOnEach(Consumer<? super Signal<T>> signalConsumer)
``` 

This one adds behavior when the `Flux` completes with an error matching the given exception type (the `Consumer` is executed first, then the `onError` signal is propagated):
```java
<E extends Throwable> Flux<T> doOnError(Class<E> exceptionType, Consumer<? super E> onError)
``` 

This version adds behavior triggered when the `Flux` completes with an error:
```java
Flux<T> doOnError(Consumer<? super Throwable> onError)
``` 

This other version adds behavior when the `Flux` completes with an error matching the given predicate:
```java
Flux<T> doOnError(Predicate<? super Throwable> predicate, Consumer<? super Throwable> onError)
``` 

Moving on, this hook adds behavior triggered when the `Flux` emits an item successfully:
```java
Flux<T> doOnNext(Consumer<? super T> onNext)
``` 

This one adds behavior triggering a `LongConsumer` when the `Flux` receives any request:
```java
Flux<T> doOnRequest(LongConsumer consumer)
``` 

This adds behavior when the `Flux` is being subscribed, that is to say when a `Subscription` has been produced by the `Publisher` and is being passed to the `Subscriber.onSubscribe(Subscription)`:
```java
Flux<T> doOnSubscribe(Consumer<? super Subscription> onSubscribe)
``` 

And this adds behavior when the `Flux` terminates, either by completing with a value, completing empty or failing with an error:
```java
Flux<T> doOnTerminate(Runnable onTerminate)
``` 

These hooks do not change the sequence’s data, so most of the time, they are used to isolate *side effects* like logging.

Also, notice that, except for `doFirst` and `doFinally`, the names of all these hooks start with `doOn`.

However, `Mono` and `Flux` have basically the same hooks. The only exception is the hook to add behavior when the sequence completes successfully. For `Mono` is  `doOnSuccess`, which takes the element emitted, whereas for `Flux` is `doOnComplete`, which doesn't take the elements emitted.

Here's an example for `Mono`:
```java
Mono.just(1)
    .log()
    .doOnSuccess(
        i -> System.out.println(
                "Mono completed successfully: " + i)
    )
    .subscribe();
``` 

This is the result:
```
[ INFO] (main) | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
[ INFO] (main) | request(unbounded)
[ INFO] (main) | onNext(1)
Mono completed successfully: 1
[ INFO] (main) | onComplete()
``` 

And here's an example for `Flux`:
```java
Flux.just(1, 2, 3)
    .log()
    .doOnComplete(
        () -> System.out.println(
                "Flux completed successfully")
    )
    .subscribe();
``` 

And this is the result:
```
[ INFO] (main) | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[ INFO] (main) | request(unbounded)
[ INFO] (main) | onNext(1)
[ INFO] (main) | onNext(2)
[ INFO] (main) | onNext(3)
[ INFO] (main) | onComplete()
Flux completed successfully 
``` 

Notice that we still need to subscribe, otherwise, nothing will be executed.

Another thing to notice is that although some methods seem to be the same, some of them take a `Runnable` while others take either the value or the signal emitted.

The following example adds print statements to some hooks so you can see at what point they are executed:
```java
Flux.just(1, 2, 3)
    .log()
    .doOnSubscribe(
        subscription -> 
            System.out.println("doOnSubscribe: " 
                                  + subscription)
    )
    .doOnRequest(
        l -> 
            System.out.println("doOnRequest: " 
                                  + l)
    )
    .doFirst(
        () -> 
            System.out.println("doFirst")
    )
    .doOnNext(
        i ->
            System.out.println("doOnNext: " 
                                  + i)
    )
    .doOnEach(
        integerSignal -> 
            System.out.println("doOnEach: " 
                                  + integerSignal)
    )
    .doFinally(
        signalType ->
            System.out.println("doFinally: " 
                                  + signalType)
    )
    .doAfterTerminate(
        () -> 
            System.out.println("doAfterTerminate")
    )
    .doOnComplete(
        () -> 
            System.out.println("doOnComplete")
    )
    .doOnTerminate(
        () -> 
            System.out.println("doOnTerminate")
    )
    .subscribe();
``` 

This is the result:
```
doFirst
[ INFO] (main) | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
doOnSubscribe: reactor.core.publisher.FluxPeekFuseable$PeekFuseableSubscriber@67c27493
doOnRequest: 9223372036854775807
[ INFO] (main) | request(unbounded)
[ INFO] (main) | onNext(1)
doOnNext: 1
doOnEach: doOnEach_onNext(1)
[ INFO] (main) | onNext(2)
doOnNext: 2
doOnEach: doOnEach_onNext(2)
[ INFO] (main) | onNext(3)
doOnNext: 3
doOnEach: doOnEach_onNext(3)
[ INFO] (main) | onComplete()
doOnEach: onComplete()
doOnComplete
doOnTerminate
doAfterTerminate
doFinally: onComplete
``` 

The first hook that is executed is `doFirst`. It takes a `Runnable` so use this hook if you want to execute something before subscribing.

After the subscription is made, `doOnSubscribe` is executed, receiving the `Subscription` object.

Next, `doOnRequest`, receiving a very big number (the value of `Long.MAX_VALUE`). That's why, in practical terms, the log shows the request as unbounded.

Then, the `Flux` starts emitting values, calling `onNext`, `doOnNext`, and `doOnEach` (in that order) for each of these values.

After the last value, there's a last call to `doOnEach` showing the `onComplete` signal.

Finally, `doOnComplete`,  `doOnTerminate`, `doAfterTerminate`, and `doFinally` are executed in that order.
