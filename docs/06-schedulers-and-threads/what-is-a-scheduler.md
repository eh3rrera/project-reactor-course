---
layout: default
title: What is a Scheduler?
parent: Schedulers and Threads
nav_order: 1
---

# What is a Scheduler?
---

The concept of a scheduler is closely related to the concept of a thread.

A thread is usually defined as a lightweight process, but it can also be seen as a path of execution in a program.

Every Java application runs on at least, one thread, usually the *main* application thread.

![main thread](images/100.png)

However, you don't always have to worry about this. In the case of Reactor, it doesn't enforce a particular threading model. 

Remember, everything starts when you subscribe. This way, by default, most operators run on the thread on which the subscription is made. 

Consider this example:
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
    .subscribe(i -> 
        System.out.format(
                "subscribe(%d) - %s\n", 
                i, 
                Thread.currentThread().getName()
        )
    );
```

At every step of the sequence, it prints the name of the thread on which the operator or method is executing.

This is the result:
```
map(1) - main
flatMap(10) - main
subscribe(100) - main
map(2) - main
flatMap(20) - main
subscribe(200) - main
map(3) - main
flatMap(30) - main
subscribe(300) - main
map(4) - main
flatMap(40) - main
subscribe(400) - main
map(5) - main
flatMap(50) - main
subscribe(500) - main
```

As you can see, everything is executed on the *main* thread, the thread where the subscription is made.

If we modify the example to start the subscription in another thread:
```java
Flux<Integer> integerFlux = 
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
        });

Thread myThread = new Thread(() ->
    integerFlux
        .subscribe(i ->
            System.out.format(
                    "subscribe(%d) - %s\n",
                    i,
                    Thread.currentThread().getName()
            )
        )
);

myThread.start();
myThread.join(); // So the program can wait for this thread to finish
```

This will be the result:
```
map(1) - Thread-0
flatMap(10) - Thread-0
subscribe(100) - Thread-0
map(2) - Thread-0
flatMap(20) - Thread-0
subscribe(200) - Thread-0
map(3) - Thread-0
flatMap(30) - Thread-0
subscribe(300) - Thread-0
map(4) - Thread-0
flatMap(40) - Thread-0
subscribe(400) - Thread-0
map(5) - Thread-0
flatMap(50) - Thread-0
subscribe(500) - Thread-0
```

Now everything is executed on `Thread-0` instead of on the `main` thread.

However, some operators can change the thread on which an operator or set of operators are executed.

One of these operators is `delayElements`, we've used it before to delay the emission of a sequence of elements by a given duration.

If we modify the first example to add this operator between `map` and `flatMap`, as well as a `Thread.sleep(1000)` statement to give time to the program to execute:
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
    .delayElements(Duration.ofMillis(10))
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
                        Thread.currentThread().getName()
                    )
    );
Thread.sleep(1000);
```

This will be the result:
```
map(1) - main
map(2) - main
map(3) - main
map(4) - main
map(5) - main
flatMap(10) - parallel-1
subscribe(100) - parallel-1
flatMap(20) - parallel-2
subscribe(200) - parallel-2
flatMap(30) - parallel-3
subscribe(300) - parallel-3
flatMap(40) - parallel-4
subscribe(400) - parallel-4
flatMap(50) - parallel-5
subscribe(500) - parallel-5
```

As you can see, after `delayElements`, `flatMap`, and `subscribe` were executed on a different thread for each element: `parallel-1`, `parallel-2`, `parallel-3`, `parallel-4`, and `parallel-5`.

In the [documentation of delayElements](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delayElements-java.time.Duration-), we can read:
> Delay each of these Flux elements (Subscriber.onNext(T) signals) by a given Duration. *Signals are delayed and continue on the parallel default Scheduler*, but empty sequences or immediate error signals are not delayed.

With a `Scheduler`, you can change the thread on which the sequence is executed.

Actually, `delayElement` (for `Mono`) and `delayElements` have a version that takes the `Scheduler` on which the delayed sequence will run:
```java
// For Mono
Mono<T> delayElement(
    Duration delay,
    Scheduler timer
)

// For Flux
Flux<T> delayElements(
    Duration delay, 
    Scheduler timer
)
```

Other operators that can take a `Scheduler`, and therefore, can be executed on a different thread are [delaySequence](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delaySequence-java.time.Duration-reactor.core.scheduler.Scheduler-), [delaySubscription](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delaySubscription-java.time.Duration-reactor.core.scheduler.Scheduler-), and [interval](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#interval-java.time.Duration-reactor.core.scheduler.Scheduler-), just to mention a few.

But how do you create a `Scheduler`?

Well, first of all, a [Scheduler](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Scheduler.html) is an abstraction similar to [ExecutorService](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html), which automatically manages a pool of threads and provides an API for running asynchronous tasks, but to execute operators. In fact, some implementations use internally `ExecutorService`.

The [Schedulers](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html) class provides many factory methods to create different types of `Scheduler` implementations:
- `Scheduler.parallel()` returns a `Scheduler` suited for parallel work that works with a fixed pool of single-threaded `ExecutorService`-based workers.
- `Scheduler.immediate()` returns a `Scheduler` that executes tasks immediately, running them on the thread that submitted them (the thread on which an operator is currently processing).  This `Scheduler` is used as a [null object](https://en.wikipedia.org/wiki/Null_object_pattern) when you require a `Scheduler` that doesn't change the thread on which the operator is executing.
- `Scheduler.single()` returns a `Scheduler` that works with a single-threaded `ExecutorService`-based worker. In other words, this `Scheduler` works with a single, reusable thread.
- `Scheduler.boundedElastic()` returns a `Scheduler` that dynamically creates a bounded number of `ExecutorService`-based workers, reusing them once they have been shut down. There's also a `Scheduler.elastic()` method that does something similar but it's now deprecated. The difference is that with `elastic()` the maximum number of created thread pools is unbounded, while with `boundedElastic()` the maximum number of created threads is bounded by a cap (by default ten times the number of available CPU cores, see [DEFAULT_BOUNDED_ELASTIC_SIZE](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#DEFAULT_BOUNDED_ELASTIC_SIZE)). Besides, with `boundedElastic()`, the maximum number of task submissions that can be enqueued and deferred on each of these backing threads is bounded (by default 100K additional tasks, see [DEFAULT_BOUNDED_ELASTIC_QUEUESIZE](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#DEFAULT_BOUNDED_ELASTIC_QUEUESIZE)). After those limits, a [RejectedExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RejectedExecutionException.html) is thrown.
- `Scheduler.fromExecutorService(ExecutorService)` returns a `Scheduler` which uses the given `ExecutorService` to schedule `Runnable` implementations.

In general, a method prefixed with `new` (like [newSingle](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#newSingle-java.lang.String-)) returns a new instance of that particular `Scheduler` implementation, while other methods like `parallel()` return an instance that will be created on the first call and cached for subsequent calls until it is disposed of. 

This way, instead of relying on the default `Scheduler` that some operators use, we can create a custom `Scheduler` and pass it to the operators that give you the option to provide one.

For example, we can pass a bounded elastic `Scheduler` to the [delaySubscription](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#delaySubscription-java.time.Duration-reactor.core.scheduler.Scheduler-) operator:
```java
Flux.just(1, 2, 3, 4, 5)
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName());
        return i * 10;
    })
    .flatMap(i -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                i,
                Thread.currentThread().getName());
        return Mono.just(i * 10);
    })
    .delaySubscription(
        Duration.ofMillis(10), Schedulers.boundedElastic()
    )
    .subscribe(i -> System.out.format(
                        "subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName()
                    )
    );
Thread.sleep(1000);
```

Here's the result:
```
map(1) - boundedElastic-1
flatMap(10) - boundedElastic-1
subscribe(100) - boundedElastic-1
map(2) - boundedElastic-1
flatMap(20) - boundedElastic-1
subscribe(200) - boundedElastic-1
map(3) - boundedElastic-1
flatMap(30) - boundedElastic-1
subscribe(300) - boundedElastic-1
map(4) - boundedElastic-1
flatMap(40) - boundedElastic-1
subscribe(400) - boundedElastic-1
map(5) - boundedElastic-1
flatMap(50) - boundedElastic-1
subscribe(500) - boundedElastic-1
```

By default, the delay is introduced through a parallel shared `Scheduler` (`parallel-X`). But as you can see, now is executed on a bounded elastic `Scheduler` (`boundedElastic-1`).

However, for the operators that don't take a `Scheduler` as a parameter, Reactor provides two methods to modify the `Scheduler` a reactive sequence is executed on: `publishOn` and `subscribeOn`.

Let's review `publishOn` first.
