---
layout: default
title: Handling Blocking Calls
parent: Working with Blocking Calls
nav_order: 2
---

# Handling Blocking Calls
---

You've learned how to use the `block*` methods to return synchronous types.

But what if you want to keep working with `Mono` and `Flux` **while** still calling blocking code?

Consider the following method as an example of blocking code:
```java
String blockingMethod() {
    String data = null;
    try {
        Path path = Paths.get(
                getClass()
                        .getClassLoader()
                        .getResource("1.txt")
                        .toURI()
        );

        try(Stream<String> lines = Files.lines(path)) {
            data = lines.collect(Collectors.joining("\n"));
        }
    } catch(Exception e) {
        e.printStackTrace();
        data = "0";
    }
    return data;
}
```

It reads all the lines of a text file from the classpath and returns its content as a `String`.

We cannot execute this code inside an operator or as the argument of `Mono.just` (not even lazily), for example:
```java
Mono.fromSupplier(() -> blockingMethod())
    .subscribe(System.out::println);
```

I mean, the code will run fine if you run it on your IDE or in the command line.

However, the problem with running blocking code in a reactive environment is the risk of running out of threads and suffering from thread starvation.

In a blocking and synchronous environment, the code can make blocking calls because each request is handled by a thread, so the blocking code won’t block other requests.

In contrast, in a reactive environment, only a few threads are handling all the requests, so if one or more are blocked, this can cause problems fast.

So the key to running blocking code in a reactive environment is doing it on a special thread pool. In other words, run the blocking code on a thread different from the one where you run the asynchronous code.

Here's the recommended way to do this:
```java
Mono<T> mono = Mono.fromCallable(() -> { // 1
    /* blocking, synchronous code */ 
});
mono.subscribeOn(Schedulers.boundedElastic()); // 2
```

- First, wrap the blocking call in a `Mono` using the method `Mono.fromCallable(Callable)`. (`1`)
- Then, using `subscribeOn`, you make sure that `Mono` is executed on a thread from `Schedulers.boundedElastic()`. (`2`)

Let me explain this step by step.

We use `Mono` because, most of the time, the blocking code will return one object (a `List` containing many elements is still one object). 

Also, for this reason, we use `Callable`, since this interface allows us to return a value:
```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```

If the blocking code you're calling doesn't return a value, you can use a `Runnable` with `Mono.fromRunnable(Runnable)`.

In any case, you now have an asynchronous type to work with, but remember, we need to solve the thread starvation issue too. So we'll need a `Scheduler` to run the blocking code on a different thread.

We could use `publishOn` to switch to another `Scheduler`, but remember, the place where `publishOn` appears in the sequence matters. If you return the `Mono` and then, the caller adds to the chain another `publishOn` operator, things may not go as planned. This is less likely to happen with `subscribeOn`, since it ensures that the `Scheduler` we specify will be used from the beginning of the chain up to the next occurrence of `publishOn`.

Finally, the recommendation is to use a bounded elastic scheduler because it is optimized for long executions and the number of active threads is capped.

Previously, `Schedulers.elastic()` was the recommended choice. However, since this scheduler allows the number of active threads to grow indefinitely, its use in highly concurrent environments will result in an explosion of threads and memory. Using `Schedulers.fromExecutorService(ExecutorService)` can be an alternative, but since a bounded elastic scheduler also queues tasks when the limit is reached, in general, it's a better alternative.

By the way, the limits can be customized with these versions of the method `Schedulers.newBoundedElastic()`:
```java
static Scheduler newBoundedElastic(
    int threadCap,
    int queuedTaskCap,
    String name
)

static Scheduler newBoundedElastic(
    int threadCap,
    int queuedTaskCap,
    String name,
    int ttlSeconds
)

static Scheduler newBoundedElastic(
    int threadCap,
    int queuedTaskCap,
    String name,
    int ttlSeconds,
    boolean daemon
)

public static Scheduler newBoundedElastic(
    int threadCap,
    int queuedTaskCap,
    ThreadFactory threadFactory,
    int ttlSeconds
)
```

Where: 
- `threadCap` is the maximum number of underlying threads to create.
- `queuedTaskCap` is the maximum number of tasks to enqueue when no more threads can be created. It can be `Integer.MAX_VALUE` for unbounded enqueueing.
- `name` is the thread name prefix.
- `ttlSeconds` is the time-to-live for an idle `Scheduler.Worker`.
- `daemon` is a flag to create backing threads as daemon threads.
- `threadFactory` is the `ThreadFactory` to use for each thread initialization.

So following this pattern, this would be the proper way to execute our blocking method:
```java
Mono.fromCallable(() -> blockingMethod())
    .subscribeOn(Schedulers.boundedElastic())
    .subscribe(System.out::println);
```

If we add an asynchronous operator (like `map`) and a statement to print the name of the thread executing each operator:
```java
Mono.fromCallable(() -> {
            System.out.println(
                "fromCallable: " 
                    + Thread.currentThread().getName()
            );
            return blockingMethod();
        })
        .subscribeOn(Schedulers.boundedElastic())
        .map(s -> {
            System.out.println(
                "map: " 
                    + Thread.currentThread().getName()
            );
            return s;
        })
        .subscribe(System.out::println);
// To give time to the program to execute
Thread.sleep(500);
```

This will be the result:
```java
fromCallable: boundedElastic-1
map: boundedElastic-1
1
```

Now, you might be wondering, both operators were executed on the same thread, isn't this what we are trying to avoid?

Well, yes, but it turns out that our blocking method executes so fast that the same thread is free to execute the `map` operator. 

That's because a bounded elastic scheduler can reuse threads from the pool. From the [documentation](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#boundedElastic--):
> By order of preference, threads backing a new Scheduler.Worker are picked from the idle pool, created anew, or reused from the busy pool. In the latter case, a best effort attempt at picking the thread backing the least amount of workers is made.

If we add a `Thread.sleep` statement to our blocking method so it can take more time to execute:
```java
String blockingMethodWithSleep() {
    String data = null;
    try {
        Path path = Paths.get(
                getClass()
                        .getClassLoader()
                        .getResource("1.txt")
                        .toURI()
        );
        
        Thread.sleep(1000);
        
        try(Stream<String> lines = Files.lines(path)) {
            data = lines.collect(Collectors.joining("\n"));
        }
    } catch(Exception e) {
        e.printStackTrace();
        data = "0";
    }
    return data;
}
```

And create five subscribers to simulate five requests:
```java
Mono<String> mono = Mono.fromCallable(() -> {
        System.out.println(
            "fromCallable: " 
                    + Thread.currentThread().getName()
        );
        return blockingMethodWithSleep();
    })
    .subscribeOn(Schedulers.boundedElastic())
    .map(s -> {
        System.out.println(
            "map: " + Thread.currentThread().getName()
        );
        return s;
    });

for(int i = 0; i < 5; i++) {
    new Thread(() -> 
               mono.subscribe(System.out::println)
    ).start();
}
// To give time to the program to execute
Thread.sleep(5000);
```

This would be the result:
```
fromCallable: boundedElastic-1
fromCallable: boundedElastic-3
fromCallable: boundedElastic-2
fromCallable: boundedElastic-5
fromCallable: boundedElastic-4
map: boundedElastic-1
1
map: boundedElastic-3
1
map: boundedElastic-2
1
map: boundedElastic-4
1
map: boundedElastic-5
1
```

As you can see, each request is executed on a different thread, so one request cannot affect the others.
