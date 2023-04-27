---
layout: default
title: Using Other Reactive Operators
nav_order: 5
has_children: true
---

# Using Other Reactive Operators
* * *
In this module, we're going to review operators for:
- Transforming sequences (in addition to map and flatMap)
- Combining publishers
- Combining tasks or actions
- Filtering sequences
- Branching on empty sequences
- Aggregating a Flux
- Listening to lifecycle hooks

For each operator, I'm going to present its description, its syntax, a few examples, and optionally, how it's related to other operators. Also, for the first sections, I'll show you the marble diagrams of some operators so you can better understand them.

You can find the code for this module [here](https://github.com/eh3rrera/project-reactor-course/tree/main/04).

After this module, you'll know many of the most used operators. However, there will be a lot more to learn. The best resource is [Appendix A: Which operator do I need?](https://projectreactor.io/docs/core/release/reference/#which-operator), from the [reference documentation](https://projectreactor.io/docs/core/release/reference/). I recommend you to, at least, browse through all the subsections so you can better know all the operators provided by Reactor.

## Links
- [What's the difference between flatMap, flatMapSequential and concatMap in Project Reactor?](https://stackoverflow.com/a/71972337/3593852)
- [Reactive Programming: The Hitchhiker’s Guide to map operators](https://medium.com/digitalfrontiers/reactive-programming-the-hitchhikers-guide-to-map-operators-7d8bbc1d8465)
- [Tuple2<T1,T2>](https://projectreactor.io/docs/core/release/api/reactor/util/function/Tuple2.html)
- [Appendix A: Which operator do I need?](https://projectreactor.io/docs/core/release/reference/#which-operator)
- [Javadoc for Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)
- [Javadoc for Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)

