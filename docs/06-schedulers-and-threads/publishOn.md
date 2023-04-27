---
layout: default
title: Using publishOn
parent: Schedulers and Threads
nav_order: 2
---

# Using publishOn
---
This operator allows you to modify the threading context (`Scheduler`) of the downstream operators in a reactive sequence, meaning that the signals `onNext`, `onComplete`, and `onError` will run on the provided `Scheduler`.

Here's the definition of this operator for `Mono`:
```java
Mono<T> publishOn(Scheduler scheduler)
```

It runs `onNext`, `onComplete` and `onError` on the supplied `Scheduler`.

For `Flux`, there are three versions of this operator that run `onNext`, `onComplete` and `onError` on the supplied `Scheduler`, discarding elements internally queued for backpressure upon cancellation or error triggered by a data signal.

The first version only takes the `Scheduler`:
```java
Flux<T> publishOn(
    Scheduler scheduler
)
```
    
The seconds one takes an additional `prefetch` argument that defines the asynchronous boundary capacity:
```java
Flux<T> publishOn(
    Scheduler scheduler, 
    int prefetch
)
```
    
And the third version takes and additional `delayError` argument that indicates if the buffer is consumed before forwarding any error:
```java
Flux<T> publishOn(
    Scheduler scheduler, 
    boolean delayError, 
    int prefetch
)
```

The operators after `publishOn` will keep using the specified threading context up to a new occurrence of this operator. So the place of `publishOn` matters.

Let's review an example:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format("map(%d) - %s\n",
                i,
                Thread.currentThread().getName());
        return i * 10;
    })
    .publishOn(
        Schedulers.newSingle("singleScheduler")
    )
    .flatMap(i -> {
        System.out.format("flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName());
        return Mono.just(i * 10);
    })
    .subscribe(i -> System.out.format(
                        "subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName()
                    )
    );
Thread.sleep(1000);
```

Between the `map` and `flatMap` operators, there's a call to `publishOn` that will change the threading context to a new single `Scheduler`.

You can see this when running the program:
```
map(1) - main
map(2) - main
flatMap(10) - singleScheduler-1
map(3) - main
map(4) - main
map(5) - main
subscribe(100) - singleScheduler-1
flatMap(20) - singleScheduler-1
subscribe(200) - singleScheduler-1
flatMap(30) - singleScheduler-1
subscribe(300) - singleScheduler-1
flatMap(40) - singleScheduler-1
subscribe(400) - singleScheduler-1
flatMap(50) - singleScheduler-1
subscribe(500) - singleScheduler-1
```

Now, if we add another call to `publishOn` before the `map` operator:
```java
Flux.just(1, 2, 3, 4, 5)
    .publishOn(
        Schedulers.newParallel("parallelScheduler")
    )
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .publishOn(
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
Thread.sleep(1000);
```

`map` will be executed on a parallel `Scheduler`, while `flatMap` and the subscription will be executed on the context specified by the latest call to `publishOn`:
```
map(1) - parallelScheduler-1
map(2) - parallelScheduler-1
flatMap(10) - singleScheduler-2
subscribe(100) - singleScheduler-2
map(3) - parallelScheduler-1
map(4) - parallelScheduler-1
map(5) - parallelScheduler-1
flatMap(20) - singleScheduler-2
subscribe(200) - singleScheduler-2
flatMap(30) - singleScheduler-2
subscribe(300) - singleScheduler-2
flatMap(40) - singleScheduler-2
subscribe(400) - singleScheduler-2
flatMap(50) - singleScheduler-2
subscribe(500) - singleScheduler-2
```

Similarly, if we move the second call to `publishOn` after the `flatMap` operator:
```java
Flux.just(1, 2, 3, 4, 5)
    .publishOn(
        Schedulers.newParallel("parallelScheduler")
    )
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
    .publishOn(
        Schedulers.newSingle("singleScheduler")
    )
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );
Thread.sleep(1000);
```

Only the subscription will happen on the single `Scheduler`:
```
map(1) - parallelScheduler-2
flatMap(10) - parallelScheduler-2
map(2) - parallelScheduler-2
flatMap(20) - parallelScheduler-2
subscribe(100) - singleScheduler-3
subscribe(200) - singleScheduler-3
map(3) - parallelScheduler-2
flatMap(30) - parallelScheduler-2
map(4) - parallelScheduler-2
flatMap(40) - parallelScheduler-2
subscribe(300) - singleScheduler-3
subscribe(400) - singleScheduler-3
map(5) - parallelScheduler-2
flatMap(50) - parallelScheduler-2
subscribe(500) - singleScheduler-3
```

A final note. Switching between threads hurts performance, it's a heavy operation. For that reason, you should only use `publishOn` when you **really** need to move the execution of a sequence to another `Scheduler` (thread).
