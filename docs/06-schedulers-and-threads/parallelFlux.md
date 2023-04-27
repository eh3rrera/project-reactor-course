---
layout: default
title: Parallelizing work with ParallelFlux
parent: Schedulers and Threads
nav_order: 4
---

# Parallelizing work with ParallelFlux
---
In Reactor, you can easily parallelize work with `ParallelFlux`, which publishes elements to an array of `Subscribers` in parallel *rails* or *groups*.

![rails](images/101.png)
 
`ParallelFlux` is another implementation of the `Publisher` interface but is not a subclass of `Flux`, it implements its own versions of operators such as `concatMap`, `filter`, `map`, and `flatMap`, just to mention a few. You can review all the operators it implements on its [javadoc](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html) page (in particular, it does not have a `doFinally` lifecycle hook). 

Having said that, you create a `ParallelFlux` from an existing `Flux` using the `parallel` method:
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel()
    .subscribe();
```

However, this only divides the sequence into multiple sub-sequences (the *rails*), but does not change the execution model.

If we add a statement to print the name of the current thread to the above example:
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel()
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );
```

This will be the result:
```
subscribe(1) - main
subscribe(2) - main
subscribe(3) - main
subscribe(4) - main
subscribe(5) - main
```

As you can see, it executes the sequence on the *main* (default) thread.

If we want to parallelize the work, we have to tell `ParallelFlux` where to run each *rail* with the `runOn` operator, which takes the `Scheduler` where the operators downstream will be executed.

There are two versions of `runOn`. The first one specifies where each *rail* will observe its incoming values with possible work-stealing and default prefetch amount:
```java
ParallelFlux<T> runOn(Scheduler scheduler)
```

While the second one allows you to specify the prefetch amount:
```java
ParallelFlux<T> runOn(Scheduler scheduler, int prefetch)
```

The `prefetch` parameter specifies the number of values to request on each *rail* from the source. The default prefetch value is defined by [Queues.SMALL_BUFFER_SIZE](https://projectreactor.io/docs/core/release/api/reactor/util/concurrent/Queues.html#SMALL_BUFFER_SIZE):
```java
public static final int SMALL_BUFFER_SIZE = 
    Math.max(16, 
             Integer.parseInt(
                System.getProperty("reactor.bufferSize.small", "256")
             )
);
```

About the `Scheduler`, the best option is to pass a parallel `Scheduler`.

Here's an example:
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel()
    .runOn(
        Schedulers.parallel()
    )
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

This is the result:
```
map(1) - parallel-1
flatMap(10) - parallel-1
map(2) - parallel-2
flatMap(20) - parallel-2
map(4) - parallel-4
flatMap(40) - parallel-4
map(5) - parallel-5
flatMap(50) - parallel-5
map(3) - parallel-3
flatMap(30) - parallel-3
subscribe(300) - parallel-3
subscribe(100) - parallel-1
subscribe(500) - parallel-5
subscribe(200) - parallel-2
subscribe(400) - parallel-4
```

This time, everything is run in five parallel threads.

But just like `publishOn`, the place of `runOn` matters.

If we move `runOn` after `map`:
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel()
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .runOn(
        Schedulers.parallel()
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

This will be the result:
```
map(1) - main
map(2) - main
flatMap(10) - parallel-1
map(3) - main
subscribe(100) - parallel-1
map(4) - main
flatMap(30) - parallel-3
subscribe(300) - parallel-3
flatMap(20) - parallel-2
flatMap(40) - parallel-4
subscribe(400) - parallel-4
map(5) - main
subscribe(200) - parallel-2
flatMap(50) - parallel-5
subscribe(500) - parallel-5
```

As you can see, `map` is executed on the *main* thread, unlike `flatMap` and `subscribe`.

By default, `ParallelFlux` splits the sequence into the total number of available CPU cores in a round-robin fashion. However, there are other two versions of `parallel` to customize this. The first one prepares a `Flux` by dividing data on a number of *rails* matching the provided parallelism parameter in a round-robin fashion:
```java
ParallelFlux<T> parallel(
    int parallelism
)
```

The second one uses a custom prefetch amount and queue for dealing with the source `Flux`'s values:
```java
ParallelFlux<T> parallel(
    int parallelism, 
    int prefetch
)
```

Once again, the default prefetch value is defined by [Queues.SMALL_BUFFER_SIZE](https://projectreactor.io/docs/core/release/api/reactor/util/concurrent/Queues.html#SMALL_BUFFER_SIZE).

This way, we can create only two *rails* to execute the sequence in two parallel threads by using `parallel(2)`: 
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel(2)
    .runOn(
        Schedulers.parallel()
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
    .subscribe(i -> System.out.format(
            "subscribe(%d) - %s\n",
            i,
            Thread.currentThread().getName())
    );
Thread.sleep(1000);
```

This is the result:
```
map(2) - parallel-2
flatMap(20) - parallel-2
subscribe(200) - parallel-2
map(4) - parallel-2
flatMap(40) - parallel-2
subscribe(400) - parallel-2
map(1) - parallel-1
flatMap(10) - parallel-1
subscribe(100) - parallel-1
map(3) - parallel-1
flatMap(30) - parallel-1
subscribe(300) - parallel-1
map(5) - parallel-1
flatMap(50) - parallel-1
subscribe(500) - parallel-1
```

As you can see, only two threads (`parallel-1` and `parallel-2`) are used this time.

In addition to `runOn`, `ParallelFlux` has other methods that you must know:
- [from(org.reactivestreams.Publisher<? extends T>)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html#from-org.reactivestreams.Publisher-) to start processing a regular Publisher in rails, where each rail covers a subset of the original `Publisher`'s data. This is an alternative to `Flux.parallel()`, which is a shortcut to achieve that on a `Flux`.
- [sequential()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html#sequential--) to merge the sources back into a single `Flux`.
- [then()](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html#then--) to emit an `onComplete` or `onError` signal once all values across rails have been observed. It returns a `Mono<Void>`.
- [subscribe(Subscriber)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html#subscribe-org.reactivestreams.Subscriber-) if you simply want to subscribe to the merged sequence. Other variants like `subscribe(Consumer)` instead do multiple subscribes, one on each rail (this means that the lambdas should be as stateless and side-effect free as possible).


For example, you can use `sequential()` to switch back to a normal `Flux` that will sequentially process the stream:
```java
Flux.just(1, 2, 3, 4, 5)
    .parallel()
    .runOn(
        Schedulers.parallel()
    )
    .map(i -> {
        System.out.format(
                "map(%d) - %s\n",
                i,
                Thread.currentThread().getName()
        );
        return i * 10;
    })
    .sequential()
    .publishOn(
        Schedulers.boundedElastic()
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

Notice that we also need to specify another `Scheduler` to process the stream sequentially with `publishOn`.

This way, `flatMap` and `subscribe` will be executed in a bounded elastic thread:
```
map(1) - parallel-3
map(3) - parallel-5
map(4) - parallel-6
map(5) - parallel-1
map(2) - parallel-4
flatMap(10) - boundedElastic-1
subscribe(100) - boundedElastic-1
flatMap(30) - boundedElastic-1
subscribe(300) - boundedElastic-1
flatMap(40) - boundedElastic-1
subscribe(400) - boundedElastic-1
flatMap(50) - boundedElastic-1
subscribe(500) - boundedElastic-1
flatMap(20) - boundedElastic-1
subscribe(200) - boundedElastic-1
```

Or with `groups()`, you can group the elements based on the index of the processing thread.

Here's an example:
```java
Flux.just(1, 2, 3, 4, 5, 6)
    .parallel(2)
    //.runOn(Schedulers.parallel())
    .groups()
    .flatMap(group -> {
        System.out.format(
                "flatMap(%d) - %s\n",
                group.key(),
                Thread.currentThread().getName()
        );
        return group.collectList();
    })
    .subscribe(l -> System.out.format(
            "subscribe(%s) - %s\n",
            l.toString(),
            Thread.currentThread().getName())
    );
```

`groups()` returns a normal `Flux` of type `Flux<GroupedFlux<Integer,T>>`, so `runOn` is optional, it won't have any effect here because we're not longer working with a `ParallelFlux`. 

This is the result:
```
flatMap(0) - main
flatMap(1) - main
subscribe([1, 3, 5]) - main
subscribe([2, 4, 6]) - main
```

With `parallel(2)`, we create two rails. Then, they are converted with `groups()` into two groups (`0` and `1`). One group will have the elements `[1, 3, 5]` and the other one the elements `[2, 4, 6]`. 

Also, notice that `flatMap` and `subscribe` are executed in the *main* (default) thread, as there's no `publishOn` operator to change the threading context.
