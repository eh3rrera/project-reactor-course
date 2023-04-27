---
layout: default
title: Using a Context
nav_order: 8
has_children: true
---

# Using a Context
* * *
In this module, you're going to learn:
- The problem a context solves
- How the Context API is organized
- How to write to and read from the context

You can find the code for this module [here](https://github.com/eh3rrera/project-reactor-course/tree/main/07).

You can also read the context from a Reactive Stream signal. The [Signal](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Signal.html) interface has a method that returns a `ContextView` instance:
```java
ContextView getContextView()
```

For more information, read the appendix C.7 from the documentation: [What Is a Good Pattern for Contextual Logging? (MDC)](https://projectreactor.io/docs/core/release/reference/#faq.mdc)

For a real-world example, Spring Security uses the Context API to provide a reactive web filter: [ReactorContextWebFilter](https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/server/context/ReactorContextWebFilter.java).

## Links
- [ThreadLocal](https://www.baeldung.com/java-threadlocal)
- [Javadoc for Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)
- [Javadoc for Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)
