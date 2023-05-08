---
layout: default
title: Upstream and downstream
parent: Working with map and flatMap
nav_order: 5
---

# Upstream and Downstream
---

When reading [Reactor's reference documentation](https://projectreactor.io/docs/core/release/reference/) or the [javadoc of the API](https://projectreactor.io/docs/core/release/api/), you'll find two terms that confused me a little bit the first time I found them: *upstream* and *downstream*.

For example, in the [javadoc of Mono.fromRunnable(Runnable)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#fromRunnable-java.lang.Runnable-), in the section *Type Parameters*, they define `T` as:
> T - The generic type of the **upstream**, which is preserved by this operator

In simple words, the *upstream* is the stream that comes *before* the current operator.

On the other hand, *downstream* is the stream that comes *after* the current operator. 

![upstream/downstream](images/67.png)

For example, consider this operator chain:
```java
Flux
  .map(...)
  .flatMap(...)
  .map(...)
  .subscribe()
```

In this case, taking the `flatMap` operator as reference, the source `Flux` and the first `map` operator would be the upstream.

Whereas the second `map` operator, as well as the `subscribe` operator, would be the downstream.

Some operators will be affected by what happens upstream or downstream, so always review the documentation to look for references to these terms.

