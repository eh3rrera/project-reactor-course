---
layout: default
title: Coding Exercises
parent: Schedulers and Threads
nav_order: 5
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/06/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/06/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/06/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/06/after/reactor-demo-exercises).

----

## Exercise 1
In this exercise, you'll create a (simulated) web scraping application that fetches the content of multiple URLs using more than one thread with a `Scheduler.boundedElastic()` scheduler. 
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;
    import reactor.core.scheduler.Schedulers;

    public class Exercise01 {
        public static void main(String[] args) throws InterruptedException {
            Flux<String> urlFlux = Flux.just("url1", "url2", "url3", "url4");

            // TODO: Change the threading context and apply the fetchAndCountWords method
            Flux<Integer> wordCountFlux = null;

            // TODO: Subscribe to the Flux and print the emitted elements

            Thread.sleep(4000);
        }

        // Simulates fetching the content of a URL and counting the number of words
        public static int fetchAndCountWords(String url) {
            int random = (int)(Math.random() * 500 + 100);
            System.out.println("Word count for " + url + ": " + random);
            return random;
        }
    }
   ```
2. Use a `Schedulers.boundedElastic()` to change the threading context and apply the `fetchAndCountWords` method. Assign the resulting `Flux` to `wordCountFlux`.
3. Subscribe to the `Flux`, printing the emitted elements with the message `Word count: {wordCount}`.
4. Run the `Exercise01` class and analyze the output.

----

## Exercise 2
In the following exercise, you'll create a `Flux` that emits a sequence of integers, simulating a fast publisher. Then, you will change the threading context to use a slow consumer that processes the emitted integers with a delay. 
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise02 {
        public static void main(String[] args) throws InterruptedException {
            Flux<Integer> fastPublisher = Flux.range(1, 5);

            // TODO: Change the threading context and apply the slowConsumer method
            Flux<Integer> processedFlux = null;

            // TODO: Subscribe to the Flux and print the emitted elements

            Thread.sleep(15000);
        }

        public static Integer slowConsumer(Integer value) {
            try {
                // Simulate a slow consumer by adding a 1-second delay
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value;
        }
    }
   ```
2. Use `Schedulers.single()` to change the threading context and apply the `slowConsumer` method. Assign the resulting `Flux` to `processedFlux`.
3. Subscribe to the `Flux`, printing the emitted elements.
4. Run the `Exercise02` class and analyze the output.

----

## Exercise 3
In this exercise, you will practice using the `subscribeOn` method with a `Flux` to handle a slow publisher, fast consumer scenario.
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise03 {
        public static void main(String[] args) throws InterruptedException {
            // TODO: Call the method slowPublisher and 
            // TODO: Use subscribeOn with a new bounding elastic scheduler
            Flux<Integer> processedFlux = null;

            // TODO: Subscribe to processedFlux passing the fastConsumer method

            Thread.sleep(11000);
        }

        private static Flux<Integer> slowPublisher() {
            return Flux.create(sink -> {
                for (int i = 1; i <= 10; i++) {
                    try {
                        // Simulate blocking IO with a 1-second delay
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sink.next(i);
                }
                sink.complete();
            });
        }

        public static void fastConsumer(Integer value) {
            System.out.println("Received: " + value);
        }
    }
   ```
2. Replace the `null` value assigned to `processedFlux` with a call the method `slowPublisher`.
3. Next, use `subscribeOn` to change the threading context to a new bounding elastic scheduler with the following parameters:
    - threadCap: `2`
    - queuedTaskCap: `2`
    - name: `bounded-elastic`
    - ttlSeconds: `30`
    - daemon: `true`
3. Subscribe to `processedFlux`, using the `fastConsumer` method to print the emitted value.
4. Run the `Exercise03` class and analyze the output.

----

## Exercise 4
In this exercise, you'll create a publisher that emits a sequence of integers, changing the threading context for processing the items, and then changing the threading context for subscribing to the publisher. 
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise05 {
        public static void main(String[] args) throws InterruptedException {
            Flux<Integer> fluxRange = Flux.range(1, 10);
            
            // TODO: Use a parallel scheduler to change the threading context
            // TODO: Apply the processInteger method
            // TODO: Change the threading context for subscribing
            Flux<Integer> processedFlux = null;

            processedFlux.subscribe(value -> System.out.println("Received: " + value));

            Thread.sleep(11000);
        }

        public static Integer processingFunction(Integer value) {
            try {
                // Simulate a task with a 500ms delay
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value * 2;
        }
    }
   ```
2. Using `fluxRange`, utilize a parallel scheduler to change the threading context.
3. Apply the `processInteger` method.
4. Then, change the threading context for subscribing using `Schedulers.single()`.
5. Run the `Exercise04` class and analyze the output.

----

## Exercise 5
In this exercise, you'll create a `Flux` that emits a sequence of integers from `1` to `10`, using a [ParallelFlux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/ParallelFlux.html ) to divide the data into multiple rails and perform the work in parallel. 
1. Given the following code:
   ```java
    import reactor.core.publisher.Flux;

    public class Exercise05 {
        public static void main(String[] args) throws InterruptedException {
            Flux<Integer> fluxRange = Flux.range(1, 10);
            
            // TODO: Create a ParallelFlux with 4 rails
            // TODO: Use Schedulers.parallel() to run the work in parallel
            // TODO: Apply the processingFunction method
            ParallelFlux<Integer> parallelFlux = null;

            parallelFlux.subscribe(value -> System.out.println("Received: " + value));

            Thread.sleep(10000);
        }

        public static Integer processingFunction(Integer value) {
            try {
                // Simulate a task with a 500ms delay
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value * 2;
        }
    }
   ```
2. Using `fluxRange`, create a `ParallelFlux` with `4` rails.
3. Use `Schedulers.parallel()` to run the work in parallel.
4. Apply the `processingFunction` method.
5. Run the `Exercise05` class and analyze the output.
 