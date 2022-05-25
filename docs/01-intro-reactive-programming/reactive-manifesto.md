---
layout: default
title: The Reactive Manifesto
parent: Introduction to Reactive Programming
nav_order: 4
---

# The Reactive Manifesto
* * *
You might have heard about the [Reactive Manifesto](https://www.reactivemanifesto.org), a document that describes a set of principles for designing systems known as Reactive Systems.

These systems have the following characteristics:
- Responsiveness. The system must provide fast and consistent response times.
- Resilience. The system must stay responsive even after failure.
- Elasticity. The system must stay responsive even after increasing workload.
- Message-driven. The system uses asynchronous messages for communication between its components.

But, as you can see, it's an architectural style. It's different from reactive programming.

You can build a reactive system without using reactive programming as long as the system is designed with the principles described above.

Yes, messages and events are both, non-blocking and asynchronous, but they refer to different things and operate at different levels of abstraction. 

A message is some data (usually an object) sent to a particular address. In this case, a message broker like [Apache Kafka](https://kafka.apache.org/) or [RabbitMQ](https://www.rabbitmq.com/) is in charge of the communication. 

On the other hand, events in reactive programming are actions or data from a source (a publisher) that the subscribers react to asynchronously using callbacks, futures, or libraries like Reactor that also offer a declarative/functional API. 

In summary, reactive programming means programming in a non-blocking, asynchronous way.

With this out of the way, let's introduce Project Reactor and set up a demo project.