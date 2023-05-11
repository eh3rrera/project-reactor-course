---
layout: default
title: Using map and flatMap
parent: Working with map and flatMap
nav_order: 4
---

# Using map and flatMap
---

A common question people often ask is when to use `map` instead of `flatMap`?

Well, to answer this, you need to ask two questions:
1. Are you transforming the elements synchronously or asynchronously?
2. Is the relationship between the input and the output elements one-to-one or one-to-many?

On the one hand, `map` uses a synchronous function to convert a value to another (possibly changing the type).

And on the other hand, `flatMap` uses an asynchronous function that returns a `Mono` or `Flux`.

This difference alone (the return type of the function passed to the operator) should be enough to choose the appropriate operator.

However, another thing to take into account is that `map` and `Mono`'s `flatMap` work with a one-to-one relationship:
- `map` converts from one to `N` number of values (in the case of `Flux`) of type `T` to another `Publisher` with the same number of elements.
- `Mono`'s `flatMap` converts a `Mono` of type `T` to a `Mono` of type `R`.

Whereas `Flux`'s `flatMap` works with a one-to-many relationship, since each element can generate a `Flux` of any number of elements.

But one thing that may not be obvious is how to properly use either `map` or `flatMap`.

For this, I have two recommendations:
- When using `Flux`'s `flatMap`, always keep in mind that the order of the elements is not guaranteed.
- You can nest operators. But don't abuse that, respect the [single-responsibility principle](https://en.wikipedia.org/wiki/Single-responsibility_principle).

The first point is often overlooked. But remember, when working with a network or an external resource such as a database asynchronously, we don't know for sure when the result will come, so the ordering cannot be guaranteed. 

`flatMapSequential()` methods manage this problem by queuing elements to maintain the order if necessary. On the other hand, `flatMapIterable()` keep the order because an `Iterable` works in a synchronous way, getting the elements from the source sequentially.

About the second point, nothing stops us from nesting `flatMap` operators. In fact, sometimes it's helpful to nest operators, in particular, if a value is needed for more than one operator:
```java
books.flatMap(
    book -> getSummary(book, "1.0")
                .flatMap(summary -> 
                            getStats(book, summary)
                )
);
```

But remember the [single-responsibility principle](https://en.wikipedia.org/wiki/Single-responsibility_principle): *A function should do only one thing*.

Whenever you see a `map` or `flatMap`  operator nested inside another `map` or `flatMap` operator:
```java
reports.flatMap(
    report -> toXLS(report)
                .flatMap(reportXLS -> save(reportXLS))
);
```

Ask yourself if it can be refactored to attach the operators in the main sequence. It will make the code more readable and easier to maintain:
```java
reports
    .flatMap(report -> toXLS(report))
    .flatMap(reportXLS -> save(reportXLS))
);
```

However, sometimes you'll need to use other operators to do this. The same happens with imperative code inside `map` or `flatMap`. Most of the time, it can be replaced by other operators. That's why, in the next module, you'll learn more Reactor operators.
