---
layout: default
title: Using subscribeOn
parent: Schedulers and Threads
nav_order: 3
---

# Using subscribeOn
---
This operator allows you to modify the threading context (`Scheduler`) of a reactive sequence at subscription time, meaning that the `onSubscribe` signal will run on the provided `Scheduler`.

Here's the definition of this operator for `Mono`:
```java
Mono<T> subscribeOn(Scheduler scheduler)
```

It runs `subscribe`, `onSubscribe`, and `request` on a specified `Scheduler`.

For `Flux`, there are two versions of this operator, which also run `subscribe`, `onSubscribe`, and `request` on a specified `Scheduler`:
```java
Flux<T> subscribeOn(
    Scheduler scheduler
)
```

For the second version, if `requestOnSeparateThread` is `true` (the default in the `subscribeOn(Scheduler)` version) the `request` method will be called on the specified `Scheduler`. It must be `false` if you are using an eager or blocking `create(Consumer, FluxSink.OverflowStrategy)` method as the source to avoid deadlocks due to requests piling up behind the emitter:
```java
Flux<T> subscribeOn(
    Scheduler scheduler, 
    boolean requestOnSeparateThread
)
```

Since `subscribeOn` applies to the subscription process, it doesn't matter where you place this operator, it will impact all operators from the beginning of the chain up to the next occurrence of `publishOn` (if any). 

Take the following piece of code as an example:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .subscribeOn(
        Schedulers.newSingle("singleScheduler")
    )
    .flatMap(i -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return Mono.just(i * 10);
    })
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );

try {
    Thread.sleep(1000);
} catch(Exception e) {}
```

With `subscribeOn`, we're setting a single `Scheduler` as the threading context. Here's the result:
```
map(1) - singleScheduler-1
flatMap(10) - singleScheduler-1
subscribe(100) - singleScheduler-1
map(2) - singleScheduler-1
flatMap(20) - singleScheduler-1
subscribe(200) - singleScheduler-1
map(3) - singleScheduler-1
flatMap(30) - singleScheduler-1
subscribe(300) - singleScheduler-1
map(4) - singleScheduler-1
flatMap(40) - singleScheduler-1
subscribe(400) - singleScheduler-1
map(5) - singleScheduler-1
flatMap(50) - singleScheduler-1
subscribe(500) - singleScheduler-1
```

The above example places `subscribeOn` between `map` and `flatMap`. If we move it after `flatMap`:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .flatMap(i -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return Mono.just(i * 10);
    })
    .subscribeOn(
        Schedulers.newSingle("singleScheduler")
    )
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );

try {
    Thread.sleep(1000);
} catch(Exception e) {}
```

The result will be the same:
```
map(1) - singleScheduler-2
flatMap(10) - singleScheduler-2
subscribe(100) - singleScheduler-2
map(2) - singleScheduler-2
flatMap(20) - singleScheduler-2
subscribe(200) - singleScheduler-2
map(3) - singleScheduler-2
flatMap(30) - singleScheduler-2
subscribe(300) - singleScheduler-2
map(4) - singleScheduler-2
flatMap(40) - singleScheduler-2
subscribe(400) - singleScheduler-2
map(5) - singleScheduler-2
flatMap(50) - singleScheduler-2
subscribe(500) - singleScheduler-2
```

It doesn't really matter where you place `subscribeOn`. 

But if there's a call to `publishOn` somewhere in the chain, from that point on, `publishOn` will change the threading context.

In the following example, `publishOn` is placed before `subscribeOn` and `flatMap`:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .publishOn(
        Schedulers.newParallel("parallelScheduler")
    )
    .subscribeOn(
        Schedulers.newSingle("singleScheduler")
    )
    .flatMap(i -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return Mono.just(i * 10);
    })
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );

try {
    Thread.sleep(1000);
} catch(Exception e) {}
```

And this is the result:
```
map(1) - singleScheduler-3
map(2) - singleScheduler-3
map(3) - singleScheduler-3
flatMap(10) - parallelScheduler-1
subscribe(100) - parallelScheduler-1
flatMap(20) - parallelScheduler-1
subscribe(200) - parallelScheduler-1
flatMap(30) - parallelScheduler-1
subscribe(300) - parallelScheduler-1
map(4) - singleScheduler-3
map(5) - singleScheduler-3
flatMap(40) - parallelScheduler-1
subscribe(400) - parallelScheduler-1
flatMap(50) - parallelScheduler-1
subscribe(500) - parallelScheduler-1
```

Finally, if there's more than one call to `subscribeOn` in the chain:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .subscribeOn(
        Schedulers.newParallel("parallelScheduler")
    )
    .subscribeOn(
        Schedulers.newSingle("singleScheduler")
    )
    .flatMap(i -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return Mono.just(i * 10);
    })
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );

try {
    Thread.sleep(1000);
} catch(Exception e) {}
```

Only the first one defined will be taken into account.

Here's the result of the above example:
```
map(1) - parallelScheduler-2
flatMap(10) - parallelScheduler-2
subscribe(100) - parallelScheduler-2
map(2) - parallelScheduler-2
flatMap(20) - parallelScheduler-2
subscribe(200) - parallelScheduler-2
map(3) - parallelScheduler-2
flatMap(30) - parallelScheduler-2
subscribe(300) - parallelScheduler-2
map(4) - parallelScheduler-2
flatMap(40) - parallelScheduler-2
subscribe(400) - parallelScheduler-2
map(5) - parallelScheduler-2
flatMap(50) - parallelScheduler-2
subscribe(500) - parallelScheduler-2
```
