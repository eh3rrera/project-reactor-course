---
layout: default
title: The Context API
parent: Using a Context
nav_order: 2
---

# The Context API
---

In practical terms, a `Context` is a collection of key/value pairs with an API similar to the one of a [Map](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html).

However, if you look at the source code of `Context`, you'll see that is an interface that extends from another interface, [ContextView](https://projectreactor.io/docs/core/release/api/reactor/util/context/ContextView.html):
```java
public interface Context extends ContextView {
    // ...
}
```

`ContextView` is an interface that defines a read-only collection of key/value pairs. It has the following methods:
```java
// Resolve a value given a key that exists within the Context, 
// or throw a NoSuchElementException if the key is not present.
T get(Object key)

// Resolve a value given a type key within the Context,
// or throw a NoSuchElementException if the key is not present.
T get(Class<T> key)

// Resolve a value given a key within the Context. 
// If unresolved return the passed default value.
T getOrDefault(Object key, @Nullable T defaultValue)

// Resolve a value given a key within the Context.
Optional<T> getOrEmpty(Object key)

// Return true if a particular key resolves to a value 
// within the Context.
boolean hasKey(Object key)

// Return true if the Context is empty.
boolean isEmpty()

// Return the size of this Context, 
// the number of pairs stored inside it.
int size()
    
// Stream key/value pairs from this Context
Stream<Map.Entry<Object,Object>> stream()

// Perform the given action for each entry
// in this ContextView. 
void forEach(BiConsumer<Object,Object> action)
```

However, most of the time, you'll work with `Context`, which extends `ContextView` to add methods to create a `Context` implementation and put new values into the context. 

About this, putting new values,  you must know that `Context` implementations are thread-safe and immutable, which means that methods that seem to add values to the context, actually create a new `Context` that contains all the current key/value pairs plus the ones you add.

This way, to create a `Context`, you can use `empty` one or of the following `of` methods (all `static`):
```java
// To return an empty Context
static Context empty()

// To create a Context out of a ContextView, 
// enabling write API on top of the read-only view.
static Context of(ContextView contextView)

// To create a Context out of a Map.
static Context of(Map<?,?> map)

// To create a Context pre-initialized with one key-value pair.
static Context of(Object key, Object value)

// To create a Context pre-initialized with two key-value pairs.
static Context of(
    Object key1, Object value1, 
    Object key2, Object value2
)

// To create a Context pre-initialized with three key-value pairs.
static Context of(
    Object key1, Object value1, 
    Object key2, Object value2, 
    Object key3, Object value3
)

// To create a Context pre-initialized with four key-value pairs.
static Context of(
    Object key1, Object value1, 
    Object key2, Object value2, 
    Object key3, Object value3, 
    Object key4, Object value4
)

// To create a Context pre-initialized with five key-value pairs.
static Context of(
    Object key1, Object value1, 
    Object key2, Object value2, 
    Object key3, Object value3, 
    Object key4, Object value4, 
    Object key5, Object value5
)
```

As you can see, some versions allow you to create a `Context` with up to five key-value pairs. 

Why five and not ten, for example?

Well, a `Context` is optimized to have five or fewer key/value pairs.

Having more could be costly in terms of performance. However, if you choose to have more than five key/value pairs, a copy-on-write implementation backed by a new `Map` will be used every time you add an entry.

The following methods of `Context` allow you to add or remove entries:
```java
// To create a new Context that contains all current
// key/value pairs plus the given one.
Context put(Object key, Object value)

// To create a new Context by merging the content of 
// this context and a given ContextView.
Context putAll(ContextView other)

// Create a new Context by merging the content of
// this context and a given Map.
Context putAllMap(Map<?,?> from)

// Create a new Context that contains all current 
// key/value pairs plus the given one only if the
// value is not null.
Context putNonNull(Object key, Object valueOrNull)

// Return a new Context that will resolve all 
// existing keys except the removed one, key.
Context delete(Object key)
```

Finally, `Context` also has a method to convert the instance to a `ContextView`, which only allows reading operations (therefore, the name of this method):
```java
ContextView readOnly()
```

As you can see, many methods of `ContextView` and `Context` are similar to the ones of a [Map](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html), so if you have worked with this interface and its implementations, you already know how to work with a `Context`.

Now, you might be wondering, how do I add a value to a `Context` so I can use it later, inside an operator?

Let's see how to do that next.
