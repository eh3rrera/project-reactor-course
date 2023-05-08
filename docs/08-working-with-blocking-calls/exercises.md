---
layout: default
title: Coding Exercises
parent: Working with Blocking Calls
nav_order: 3
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/08/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/08/before/reactor-demo-exercises).

**Note:** If you're creating a new project, you also have to add the following dependency for the first exercise:
```xml
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty-http</artifactId>
    <version>1.1.6</version>
</dependency>
```

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/08/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/08/after/reactor-demo-exercises).

----

## Exercise 1
In this exercise, you'll create a simple application that fetches the content of a URL, and then use [blockOptional(Duration timeout)](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#blockOptional--) to handle the result of the request, including the possibility of a timeout.
1. Given the code:
   ```java
    import reactor.core.publisher.Mono;
    import reactor.netty.http.client.HttpClient;
    import java.time.Duration;
    import java.util.Optional;

    public class Exercise01 {
        public static void main(String[] args) {
            String url = "https://example.com";

            // TODO: Create a Mono from the fetchUrlContent method

            // Set the timeout duration
            Duration timeout = Duration.ofSeconds(5);

            // TODO: Get an Optional using the specified timeout
            Optional<String> contentOptional = null;

            // Handle the result of the request
            contentOptional.ifPresentOrElse(
                    content -> System.out.println("URL content: " + content),
                    () -> System.out.println("The Mono completed empty.")
            );
        }

        public static Mono<String> fetchUrlContent(String url) {
            HttpClient httpClient = HttpClient.create();
            return httpClient.get()
                    .uri(url)
                    .responseSingle((response, content) ->
                            content.asString()
                    );
        }
    }
   ```
2. Create a `Mono` from the `fetchUrlContent` method.
3. Get an `Optional` from the `Mono` using the specified timeout.
4. Run the `Exercise02` class and analyze the output.

----

## Exercise 2
In this exercise, you'll transform a `Flux` into a lazy stream in a blocking way.
1. Create a class named `Exercise02` with a `main` method.
2. Generate a `Flux` from `1` to `10`.
3. Transform the `Flux` into a lazy stream blocking for each source `onNext` call.
4. Iterate over the elements of the stream and print them.
5. Run the `Exercise02` class and analyze the output.

----

## Exercise 3
In this exercise, you'll practice running blocking code in a separate thread pool.
1. Given the code:
   ```java
    public class Exercise03 {
        public static void main(String[] args) {
            // TODO: Create a Mono from the blockingOperation method

            // TODO: Run the blocking code on a bounded elastic scheduler

            // TODO: Subscribe to the Mono and print the emitted value
        }

        public static String blockingOperation() {
            try {
                // Simulate a blocking operation using Thread.sleep()
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Blocking operation completed";
        }
    }
   ```
2. Use `Mono.fromCallable` to create a `Mono` from the `blockingOperation` method.
3. Run the blocking code on a bounded elastic scheduler using `subscribeOn`.
4. Subscribe to the `Mono` and print the emitted value.
5. Run the `Exercise03` class and analyze the output.
