---
layout: default
title: The Reactive Streams Specification
parent: Working with Mono and Flux
nav_order: 1
---

# The Reactive Streams Specification
---
In the previous module, we talked about the [Reactive Streams specification](https://www.reactive-streams.org), a specification that defines a [set of standard interfaces](https://www.reactive-streams.org/reactive-streams-1.0.4-javadoc/org/reactivestreams/package-summary.html) for libraries that work with reactive (asynchronous) streams:
- `Processor`
- `Publisher`
- `Subscriber`
- `Subscription`

Here's a diagram that represents the relationship between `Publisher`, `Subscriber`, and `Subscription`:

![Diagram for Publisher, Subscriber, Subscription](images/37.png)

Let's start with `Publisher`.

According to its [javadoc](https://www.reactive-streams.org/reactive-streams-1.0.4-javadoc/org/reactivestreams/Publisher.html), the `Publisher` interface represents:
> A provider of a potentially unbounded number of sequenced elements, publishing them according to the demand received from its Subscriber(s).

Remember, in addition to being asynchronous, reactive streams have backpressure capabilities to control the number of elements received by the subscribers.

The `Publisher` interface provides one method:
```java
public interface Publisher<T> {
    public void subscribe(Subscriber<? super T> s);
}
```

This method requests publishers to start streaming (pushing) data to the `Subscriber` (or a superclass) instance passed as an argument. It can be called multiple times passing different `Subscriber` instances.

Here's the definition of the `Subscriber` interface:
```java
public interface Subscriber<T> {
    public void onSubscribe(Subscription s);
    public void onNext(T t);
    public void onError(Throwable t);
    public void onComplete();
}
```

First, the `Publisher` must create a `Subscription` object to pass it to the `onSubscribe` method on the `Subscriber`, so that this object can execute initialization logic.

When an element of the sequence is available (an object of type `T`), the `Publisher` sends it to the `Subscriber` using the `onNext(T t)` method. It keeps doing this until:
- All the requested elements have been sent. After that, the `Publisher` calls the `onComplete()` method.
- An error occurs. In this case, the `Publisher` calls the `onError(Throwable t)` method, passing the exception that represents that error.

The `Subscriber` uses the `Subscription` object to control the subscription with the `Publisher`. These are the methods of the `Subscription` interface:
```java
public interface Subscription {
    public void request(long n);
    public void cancel();
}
```

As you can see, with a `Subscription` object, the `Subscriber` can control the number of requested elements or cancel the subscription.

Here's the diagram again:

![Diagram for Publisher, Subscriber, Subscription](images/37.png)

Notice that a `Subscription` object is tied to one `Publisher` and one `Subscriber`, and this object is not shared outside of the `Subscriber`. That's why all the methods of all these interfaces return `void`. Everything is passed as arguments to the appropriate methods.

And just for completion, here's the definition of the `Processor` interface:
```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

As you can see, it combines the functionality of a `Subscriber` and a `Publisher`.

Project Reactor provides two implementations of the `Publisher` interface:
- `Mono`
- `Flux`

Let's talk about them next.
