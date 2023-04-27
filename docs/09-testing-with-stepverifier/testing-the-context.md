---
layout: default
title: Testing the Context
parent: Testing with StepVerifier
nav_order: 5
---

# Testing the Context
* * *

If you need to test an scenario that involves storing values in the context, `StepVerifier.Step` provides two methods related to the propagation of a sequence's context:
```java
// To expect that after the Subscription step, 
// a Context has been propagated.
StepVerifier.ContextExpectations<T> expectAccessibleContext()

// To expect that NO Context was propagated 
// after the Subscription phase.
StepVerifier.Step<T> expectNoAccessibleContext()
```

These methods are usually placed right after `create`, but pay attention to their return type.

The method `expectNoAccessibleContext()` tests that **no** context is propagated and returns a `StepVerifier.Step` instance so you can add expectations about the values emitted by the sequence.

On the other hand, `expectAccessibleContext()` tests that a context is propagated and returns an instance of [StepVerifier.ContextExpectations](https://projectreactor.io/docs/test/release/api/reactor/test/StepVerifier.ContextExpectations.html). This interface allows you to check the content of the context.

For example, to apply custom assertions to the propagated `Context`:
```java
StepVerifier.ContextExpectations<T> assertThat(
    Consumer<Context> assertingConsumer
)
```

To check that the propagated `Context` contains the given value associated to the given key:
```java
StepVerifier.ContextExpectations<T> contains(
    Object key, Object value
)
```

To check that the propagated `Context` contains all of the key-value pairs of the given `Map`:
```java
StepVerifier.ContextExpectations<T> containsAllOf(
    Map<?,?> other
)
```

To check that the propagated `Context` contains all of the key-value pairs of the given `Map`, and nothing else:
```java
StepVerifier.ContextExpectations<T> containsOnly(
    Map<?,?> other
)
```

To check that the propagated `Context` contains a value for the given key:
```java
StepVerifier.ContextExpectations<T> hasKey(
    Object key
)
```

To check that the propagated `Context` is of the given size:
```java
StepVerifier.ContextExpectations<T> hasSize(
    int size
)
```

Or to check that the propagated `Context` matches a given `Predicate`:
```java
StepVerifier.ContextExpectations<T> matches(
    Predicate<Context> predicate
)
```

All these methods return an instance of `StepVerifier.ContextExpectations`, so you can keep testing the content of the context. 

Once you're done, you can use the method `then()` to return a `StepVerifier.Step` instance that groups all the context checks you set up and tests expectations about the values emitted by the sequence going forward:
```java
StepVerifier.Step<T> then()
```

Let's review some examples.

Consider the following sequence:
```java
private final String KEY = "myKey";

Flux<Integer> getFlux() {
    return Flux.just(1, 2, 3, 4)
        .transformDeferredContextual(
                (flux, ctx) ->
                        flux.map(
                            i -> i * ctx.getOrDefault(KEY, 1)
                        )
        );
}
```

It uses a value stored in the context to multiply its elements.

If we call the method `expectNoAccessibleContext()` when testing this sequence:
```java
StepVerifier
    .create(getFlux())
    .expectNoAccessibleContext()
    .expectNextCount(4)
    .verifyComplete();
```

We'll get a failing test because the method `transformDeferredContextual()` expects a `Context` object.

If you change the method to `expectAccessibleContext()`:
```java
StepVerifier
    .create(getFlux())
    .expectAccessibleContext()
    .then()
    .expectNextCount(4)
    .verifyComplete();
```

Your test should pass.

However, notice that you'll also need to add the method `then()` to be able to call `expectNextCount(4)`.

Now, a `Context` object is created at subscription time, so you might be thinking, is a `Context` object created here? 

Yes.

Remember, the `verify` method subscribes to the sequence and in this case, a default (empty) `Context` is created.

We can prove this by printing the `Context` object with the `assertThat` method, which receives a `Consumer` of type `Context`:
```java
StepVerifier
    .create(getFlux())
    .expectAccessibleContext()
    .assertThat(System.out::println)
    .then()
    .expectNextCount(4)
    .verifyComplete();
```

This is what gets printed:
```java
Context0{}
```

This way, if we test, for example, if the `Context` contains an entry with our key:
```java
StepVerifier
    .create(getFlux())
    .expectAccessibleContext()
    .hasKey(KEY)
    .then()
    .expectNextCount(4)
    .verifyComplete();
```

The test will fail because the propagated `Context` is empty.

So how do we add entries to this `Context` object?

Well, the `create` method has a version that takes a `StepVerifierOptions` object:
```java
static <T> StepVerifier.FirstStep<T> create(
    Publisher<? extends T> publisher, 
    StepVerifierOptions options
)
```

`StepVerifierOptions` has two methods related to the `Context`, one to get the initial `Context` and another one to set it:
```java
// Returns the Context to be propagated 
// initially by the StepVerifier.
Context getInitialContext() 

// Set an initial Context to be propagated by 
// the StepVerifier when it subscribes to the sequence,
// returning the instance of StepVerifierOptions 
// to continue setting more options.
StepVerifierOptions withInitialContext(
    Context context
)
```

Here's an example that shows how to use it:
```java
StepVerifier
    .create(getFlux(),
        StepVerifierOptions
                .create()
                .withInitialContext(
                    Context.of(KEY, 10)
                )
    )
    .expectAccessibleContext()
    .hasKey(KEY)
    .then()
    .expectNextCount(4)
    .verifyComplete();
```

However, if we want to make sure everything is working properly, we can use the `expectNext` method to pass the exact value we're expecting:
```java
StepVerifier
    .create(getFlux(),
        StepVerifierOptions
                .create()
                .withInitialContext(
                    Context.of(KEY, 10)
                )
    )
    .expectAccessibleContext()
    .hasKey(KEY)
    .then()
    .expectNext(10, 20, 30, 40)
    .verifyComplete();
```

In any case, the test(s) should pass.

