---
layout: default
title: The Problem a Context Solves
parent: Using a Context
nav_order: 1
---

# The Problem a Context Solves
---
In the previous module, you learned that with the `Scheduler` abstraction is easy to execute a reactive sequence in different threads or in a parallel way.

However, working with multiple thread has its own set of problems.

One of them is propagating data through a sequence, where this data must be visible and accesible to every operator of the sequence, even if the execution goes from one thread to another.

![Propagating data](images/102.png)

This is a common problem in web applications or APIs.

For example, many applications use and identifier for every request, which can be sent in an HTTP header and logged at every execution point.

This way, if there's a problem or an error, we can collect the logs, and search for the request identifier to get the entire path of execution for that user request, from service A to service B, or if you want, from operator A to operator B.

There are many frameworks and libraries that can do this, but the main problem to solve is where to put this identifier.

For non-reactive applications, most solutions are implemented with [ThreadLocal](https://www.baeldung.com/java-threadlocal), which allows us to store data that can be read and written only by the thread that takes care of a request (thread-local data).

However, in a reactive application, where a request can be handled by more than one thread, and threads can also be shared between different operators, `ThreadLocal` becomes unreliable.

According to the article [The Good, the Bad, and the Ugly: Propagating Data Through Reactive Streams](https://dzone.com/articles/the-good-the-bad-and-the-ugly-of-propagating-data), two common ways of solving this issue are:
- Local variables, which are usually passed as extra parameters to methods (the bad).
- Tuples, which can group the business data and the data that you want to propagate into a single object that gets propagated downstream (the ugly).

These solutions have more downsides than benefits.

The solution Reactor provides is a feature called [Context](https://projectreactor.io/docs/core/release/api/reactor/util/context/Context.html) (the good part in the article).

A `Context` is a key/value store (like a [Map](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html)) that allows us to store data associated with a particular `Subscriber` rather than a particular thread (like `ThreadLocal` does).

This `Context` is populated at subscription time and propagated throughout the whole sequence so it can be accessed at any moment by any operator.

Let's learn more about the `Context` API.
