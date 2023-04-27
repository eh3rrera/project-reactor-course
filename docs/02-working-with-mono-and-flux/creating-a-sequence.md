---
layout: default
title: Creating a Sequence
parent: Working with Mono and Flux
nav_order: 3
---

# Creating a Sequence
---

In the [GitHub repository of this course](https://github.com/eh3rrera/project-reactor-course), go to the folder [02](https://github.com/eh3rrera/project-reactor-course/02) to find the Maven projects for this module in the `before` and `after` directories.

All right.

Until now, we've been talking about streams and sequences. But I want you to think of `Mono` and `Flux` as containers of values of a certain type (`T`). Zero or one in the case of `Mono`:

![Mono as container](images/50.png)

And from zero to N (actually, to `Long.MAX_VALUE`) in the case of `Flux`:

![Flux as container](images/51.png)

It's better to think this way since the beginning, as it will help you grasp some concepts more easily later on.

This way, to create these containers and put elements into them, there are many static factory methods available.

Let's start with the method `empty()`:
```java
Mono<String> emptyMono = Mono.empty();
Flux<String> emptyFlux = Flux.empty();
```

The above example creates an empty `Mono` of type `String` and an empty `Flux` of type `String`. If we subscribe to these publishers at this time, they will only emit a completion signal because they contain no values. However, if we ever put values into them, these values must be of type `String`.

We can put individual elements at the time of creation of the publisher with the method `just`:
```java
Mono<Integer> integerMono = Mono.just(1);
Flux<Integer> integerFlux = Flux.just(1, 2);
```

Since `Mono` can only contain one element at most, trying to pass more than one argument to this method will result in a compiler error:
```java
 // Compiler error
Mono<Integer> integerMono = Mono.just(1, 2);
```

And here's where the differences start. Look at the definition of the `just()` method for `Mono` and `Flux`:
```java
// Mono
static <T> Mono<T> just(T data)

// Flux
static <T> Flux<T> just(T... data)
static <T> Flux<T> just(T data)
```

For `Flux`, the overloaded version of `just()` allows us to create an empty container:
```java
// No error
Flux<Integer> integerFlux = Flux.just();
```
 
But for `Mono`, the `just()` method always expects an argument:
```java
// Error
Mono<Integer> integerMono = Mono.just();
```

Of course, for clarity, it's better to use the `empty()` method when we don't plan to publish any elements. However, we don't always know from the beginning if there are elements to publish. Besides, `Mono` is supposed to accept zero or one element, right?

Well, for the case when you're not sure if there's a element, `Mono` has two versions of the method `justOrEmpty()`:
```java
static <T> Mono<T> justOrEmpty(Optional<? extends T> data)
static <T> Mono<T> justOrEmpty(T data)
```

They create a new `Mono` with the specified element if the argument is not an empty `Optional` or non-null. Otherwise, it creates an empty `Mono`:
```java
// Creates an empty Mono
Mono<Integer> emptyMono1 = Mono.justOrEmpty(Optional.empty());
// Also creates an empty Mono
Mono<Integer> emptyMono2 = Mono.justOrEmpty(null); 
```

Now, you can create create `Mono` and `Flux` objects from other objects with the methods `from*()`. Of course, due to the difference in cardinality, `Mono` and `Flux` have different `from*()` methods.

There are `from*()` methods to create a `Mono` from another `Publisher`:
```java
// To create a Mono from another Publisher
static <T> Mono<T> from(Publisher<? extends T> source)
static <I> Mono<I> fromDirect(Publisher<? extends I> source)
```

To create a `Mono` from a `Callable`, `Runnable`, or a `Supplier`:
```java
static <T> Mono<T> fromCallable(Callable<? extends T> supplier)
static <T> Mono<T> fromRunnable(Runnable runnable)
static <T> Mono<T> fromSupplier(Supplier<? extends T> supplier)
```
    
From a `CompletableFuture`, eagerly or lazily (with a `Supplier`):
```java
static <T> Mono<T> fromFuture(CompletableFuture<? extends T> future)
static <T> Mono<T> fromFuture(
    Supplier<? extends CompletableFuture<? extends T>> futureSupplier
)
```

And from a `CompletionStage`, eagerly or lazily (with a `Supplier`):
```java
static <T> Mono<T> fromCompletionStage(
    CompletionStage<? extends T> completionStage
)
static <T> Mono<T> fromCompletionStage(
    Supplier<? extends CompletionStage<? extends T>> stageSupplier
)
```

I want to highlight three things here.

One. The difference between `from` and `fromDirect` is that the first one will cancel the publisher passed as an argument after the first element. Take example the following code:
```java
Flux<Integer> integerFlux = Flux.just(1, 2);
Mono<Integer> mono1 = Mono.from(integerFlux);
Mono<Integer> mono2 = Mono.fromDirect(integerFlux);
```

Both, `mono1` and `mono2` will emit only the first element of `integerFlux`, `1`. However, `mono1` will cancel the `integerFlux` after emitting `1`, while `mono2` will allow `integerFlux` to emit `2` under the hood. This can cause unintended side effects, so only use `fromDirect` if you know the publisher passed as argument only emits one element.

Two. About the method `fromRunnable`, you may be wondering, how can I generate a value for the `Mono` if the `Runnable` interface defines a method with `void` as the return value?
```java
public interface Runnable {
    public void run();
}
```

Well, most of the time, `Mono` and `Flux` are containers of values of a certain type. But with methods like `fromRunnable`, they can also contain actions. If there's no value to publish, the return type can be `Void`.

This way, we can use a `Runnable` to modify a value (as a side effect) asynchronously:
```java
private int myValue = 0;
// ...
Mono<Void> runnableMono = 
    Mono.fromRunnable(new Runnable() {
        @Override
        public void run() {
            myValue++;
        }
});
```

Or execute some action (possibly causing a side effect too):
```java
// Using a lambda expression to simplify the code
Mono<Void> runnableMono2 = Mono.fromRunnable(
        () -> System.out.println("Hello from Runnable!")
);
```

And three. When creating a `Mono` from a [CompletableFuture](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html), which also implements the interface [CompletionStage](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletionStage.html), we have the choice of creating the `Mono` either eagerly or lazily.

For example, the following code will print the string `Eager` because the `CompletableFuture` will be executed when the `Mono` is created:
```java
Mono<String> futureMonoEager = Mono.fromFuture(
    CompletableFuture.supplyAsync(() -> {
        System.out.println("Eager");
        return "Hello from eager future!";
}));
```

On the other hand, executing the following code (that uses a `Supplier` to provide the `CompletableFuture`) will not print anything because the `CompletableFuture` will be executed until we use the `Mono`:
```java
Mono<String> futureMonoLazy = Mono.fromFuture(
    () -> CompletableFuture.supplyAsync(() -> {
        System.out.println("Lazy");
        return "Hello from lazy future!";
}));
```

About `Flux`, since it can emit more than one element, we can create one from the most common objects that represent or can contain many elements.

For example, you can create a `Flux` from another `Publisher`:
```java
static <T> Flux<T> from(Publisher<? extends T> source)
```

From an array:
```java
static <T> Flux<T> fromArray(T[] array)
```

From an `Iterable`:
```java
static <T> Flux<T> fromIterable(
    Iterable<? extends T> it
)
```

Or from a `Stream`:
```java
static <T> Flux<T> fromStream(Stream<? extends T> s)
static <T> Flux<T> fromStream(
    Supplier<Stream<? extends T>> streamSupplier
)
```

Two things to highlight here.

One. [Iterable](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html) is the super interface of many other interfaces and classes, probably being [List](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html) the most used:
```java
List<Integer> myList = Arrays.asList(1, 2, 3);
Flux listFlux = Flux.fromIterable(myList);
```

And two. The difference between the two `fromStream()` methods is not about eager and lazy creation like in `Mono`'s methods `fromFuture()` and `fromCompletionStage()`. It's about reusing the stream from which the publisher is created.

As you probably know, a [Stream](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/Stream.html) will throw an exception if you try to use it after it has been operated upon or closed. For example, if we subscribe to the following `Flux` more than one time, an exception will be thrown:
```java
Stream stream = Stream.of(1, 2, 3);
Flux<Integer> streamFluxUseOneTime = Flux.fromStream(stream);
```

And that's because we'll be using the same stream, which will be closed automatically by the first subscription on cancellation, error or completion.

On the other hand, wrapping the stream in a `Supplier` will get us a new one each time we subscribe:
```java
Flux<Integer> streamFluxUseMultipleTimes = Flux.fromStream(() -> Stream.of(1, 2, 3));
```

So it doesn't matter if the stream is closed, each subscription gets a new stream.

In any case, with both methods, the stream will be executed lazily, until we subscribe to the `Flux`.

Sometimes, this lazy behavior is useful. For example, when generating the elements of a `Mono` or `Flux` is the result of an expensive operation. Or if the elements can change depending on a certain condition.

For this, we have the `defer()` method:
```java
// For Mono
static <T> Mono<T> defer(
    Supplier<? extends Mono<? extends T>> supplier
)

// For Flux
static <T> Flux<T> defer(
    Supplier<? extends Publisher<T>> supplier
)
```

These methods will execute the `Supplier` passed as an argument (to provide a `Publisher`) until a subscription is made. In other words, these methods will create the `Publisher` lazily, using any of the methods we've reviewed so far:
```java
Flux<Integer> fluxDeferred = Flux.defer( () -> Flux.just(1, 2, 3));
```

In addition to this, the `Supplier` will return a new `Publisher` for each subscription (like in the stream example above). 

Take, for instance, the following code:
```java
Mono<Integer> monoDeferred = Mono.defer(() -> Mono.just(getValue()));
// ...
private Integer getValue() {
    System.out.println("getValue()");
    return 1;
}
```

Every time you subscribe to `monoDeferred`, the method `getValue()` will be executed. But if you define the mono without `defer()`:
```java
Mono<Integer> monoNotDeferred = Mono.just(getValue());
```

The method `getValue()` will be executed only once, no matter how many times you subscribe (even if you never do it).

For most advanced usages, we also have the method `create()`:
```java
// For Mono
static <T> Mono<T> create(Consumer<MonoSink<T>> callback)
    
// For Flux
static <T> Flux<T> create(
    Consumer<? super FluxSink<T>> emitter
)
static <T> Flux<T> create(
    Consumer<? super FluxSink<T>> emitter, 
    FluxSink.OverflowStrategy backpressure
)
```

Instead of returning a `Publisher` with a `Supplier` (as with the `defer()` method), you use a [MonoSink](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/MonoSink.html) or a [FluxSink](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/FluxSink.html) object passed as the argument of a [Consumer](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Consumer.html). These objects let you emit elements or generate signals. 

In the case of `MonoSink`, to complete with the given value:
```java
void success(T value)
```

To complete without any value:
```java
void success()
```
    
And to terminate with the given exception:
```java
void error(Throwable e)
```

For `FluxSink`, to emit a non-null element, generating an `onNext` signal:
```java
FluxSink<T> next(T t)
```
    
To fail the sequence, generating an `onError` signal:
```java
void error(Throwable e)
```
    
To terminate the sequence successfully, generating an `onComplete` signal:
```java
void complete()
```

However, `Flux` also has a [generate()](https://projectreactor.io/docs/core/release/reference/#producing.generate) method for synchronous and one-by-one emissions.

For example, to programmatically create a `Flux` by generating signals one-by-one via a consumer callback:
```java
static <T> Flux<T> generate(
    Consumer<SynchronousSink<T>> generator
)
```

To programmatically create a `Flux` by generating signals one-by-one via a consumer callback, supplying an initial state value:
```java
static <T,S> Flux<T> generate(
    Callable<S> stateSupplier, 
    BiFunction<S,SynchronousSink<T>,S> generator
)
```

And to programmatically create a `Flux` by generating signals one-by-one via a consumer callback, supplying an initial state value, and a final cleanup callback:
```java
static <T,S> Flux<T> generate(
    Callable<S> stateSupplier, 
    BiFunction<S,SynchronousSink<T>,S> generator, 
    Consumer<? super S> stateConsumer
)
```

We'll leave it at that for now. Probably, I'll write one or more articles later to explain in more detail the methods `defer` and `generate`.

The important thing is that you understand that:
- `just()` methods are the simpler methods to wrap a value into a `Publisher` eagerly.
- `from*()` methods allow you to create a `Publisher` from another object, in some cases lazily.
- `defer()` allows you to create a `Publisher` lazily, with a `Supplier` expression that is evaluated until you subscribe.
- `create()` gives you full control to create a `Publisher` lazily through a `MonoSink` or a `FluxSink` object.

In any case, all the action begins when you subscribe to the `Publisher`.

Let's see how to do this.

