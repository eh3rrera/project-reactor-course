---
layout: default
title: Coding Exercises
parent: Handling Errors
nav_order: 4
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/05/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/05/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/05/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/05/after/reactor-demo-exercises).

----

## Exercise 1
This exercise simulates fetching user data from a remote API. The API can return either a list of user names or an error.
1. Given the following code where the method `simulateApiCall` simulates an API call and based on a random value, either returns a list of user names or throws an exception:
    ```java
    import reactor.core.publisher.Flux;

    import java.util.Arrays;
    import java.util.List;
    import java.util.Random;

    public class Exercise01 {
        public static void main(String[] args) {
            Flux<List<String>> userNamesFlux = Flux.defer(() -> {
                // TODO Create either a Flux with the result or with an error
                return null;
            });

            userNamesFlux.subscribe(userNames -> System.out.println("User data: " + userNames),
                    error -> System.out.println("Error fetching user data: " + error.getMessage()));
        }

        public static List<String> simulateApiCall() {
            List<String> userNames = Arrays.asList("Alice", "Bob", "Carol", "David");
            Random random = new Random();
            if (random.nextBoolean()) {
                throw new RuntimeException("Remote API error");
            }
            return userNames;
        }
    }
   ```
2. Implement the body of the lambda expression passed to `Flux.defer` to call the method `simulateApiCall`:
    - Create a `Flux` that uses the `Flux.error` method to emit an error if the `simulateApiCall` method throws an exception. 
    - Otherwise, create a `Flux` with the list returned by the method.
3. Run the `Exercise01` class and analyze the output.

----

## Exercise 2
In the following exercise, you'll implement the reactive equivalent of the following imperative `try-catch-finally` block.
1. Create a class named `Exercise02` with a `main` method.
2. Given the following code:
    ```java
    List<Integer> inputList = Arrays.asList(1, 2, -3, 4);

    try {
        for (Integer num : inputList) {
            if (num < 0) {
                throw new IllegalArgumentException("Negative numbers are not allowed.");
            }
            System.out.println(num * 2);
        }
    } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
    } finally {
        System.out.println("Finished processing the list.");
    }
   ```
3. Create a `Flux` with the values of `inputList` (if you prefer, you can create a `Flux` from this list).
4. Implement the reactive equivalent of the above `try-catch-finally` block using the `map`, `doOnError`, `doFinally`, and `subscribe` operators.
5. Run the `Exercise02` class and analyze the output.

**HINTS:** 
- Use the `map` operator to double each integer and throw an `IllegalArgumentException` if a negative number is found.
- Handle the error using the `doOnError` operator, printing the appropriate message.
- Use the `doFinally` operator to print the appropriate message regardless of whether an error occurred or not. 
- Subscribe to the `Flux` to print each doubled value, also passing an empty error consumer to avoid printing the stack trace of the exception.

----

## Exercise 3
In this exercise, you are given a list of URLs, each containing a JSON string. Your task is to create a program that fetches JSON data from the URLs and parses them into strings. Some of the URLs may return invalid JSON responses, causing a exception to be thrown during the parsing process. In case of an invalid JSON string, you should fetch data from a fallback URL, parse it, and return the result.
1. Create a class named `Exercise03` with a `main` method.
2. Given the following code were the methods `fetchData` and `parseJson` simulate the fetching and parsing of JSON data, respectively:
    ```java
    import reactor.core.publisher.Flux;

    public class Exercise03 {
        public static void main(String[] args) {
            String fallbackUrl = "fallbackUrl";

            Flux.just("url1", "url2", "url3", "url4")
                    // TODO Use flatMap/map and onErrorResume to implement the functionality
                    .subscribe(System.out::println);
        }

        public static String fetchData(String url) {
            // Simulates fetching JSON data from the URL
            return "{\"data\": \"" + url + "\"}";
        }

        public static String parseJson(String jsonString) {
            // Simulates the parsing of jsonString and throwing an exception if it is invalid
            String str = "";
            if(jsonString.contains("url3"))
                throw new RuntimeException("Invalid JSON");
            else
                str = jsonString.toUpperCase();
            return str;
        }
    }
   ```
3. Fetch and parse the JSON data handling any exception using the `onErrorResume` operator. In case of an exception, fetch data from the fallback URL, parse it, and return the result.
4. Run the `Exercise03` class and analyze the output.

**Note:** This exercise can be implemented in more than one way. Probably one of the easiest is to get, inside a `flatMap` operator, the JSON string as a `Mono`, and then, use map to parse it and `onErrorResume` to use the fallback URL. Take a look at the solution to review this approach if you're having a hard time with this exercise.

----

## Exercise 4
In this exercise, you are given a list of integers. Your task is to implement the error handling of a program that processes a number using `doOnError` and `onErrorResume`.
1. Create a class named `Exercise04` with a `main` method.
2. Given the following code:
    ```java
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    public class Exercise04 {
        public static void main(String[] args) {
            Flux.just(1, 2, 3, 4, 5)
                    .flatMap(number -> {
                        // TODO Implement reactive sequence using doOnError and onErrorResume
                        return null;
                    })
                    .subscribe(System.out::println);
        }

        public static Mono<Integer> processNumber(int number) {
            int doubled = number * 2;
            if (doubled % 4 == 0)
                return Mono.error(new IllegalArgumentException("Result is divisible by 4"));
            else
                return Mono.just(doubled);
        }
    }
   ```
3. Inside the `flatMap` operator:
  - Call the method `processNumber` to get a `Mono<Integer>`.
  - Use the `doOnError` method to print "Error processing [number]: " followed by the error message, but only for `IllegalArgumentException`.
  - Use the `onErrorResume` operator to handle any error by returning a `Mono` that emits `-1` as the fallback value.
4. Run the `Exercise04` class and analyze the output.

----

## Exercise 5
In this exercise, you'll use [Exceptions.propagate(Throwable)](https://projectreactor.io/docs/core/release/api/reactor/core/Exceptions.html) to propagate a checked exception as a `RuntimeException`.
1. Create a class named `Exercise04` with a `main` method.
2. Given the following code:
    ```java
    import reactor.core.publisher.Flux;

    public class Exercise05 {
        public static void main(String[] args) {
            Flux<String> stringFlux = Flux.just("1", "2", "three", "4", "5");

            stringFlux.map(stringNumber -> {
                        // TODO Parse each element and propagate any NumberFormatException
                        return null;
                    })
                    .subscribe(
                            System.out::println,
                            error -> System.out.println("Error: " + error.getMessage())
            );
        }
    }
   ```
3. Inside the `map` operator, parse the strings into integers.
4. If a string is not a valid number, throw a `NumberFormatException` and propagate it as a `RuntimeException` using `Exceptions.propagate(Throwable)` method.
5. Run the `Exercise05` class and analyze the output.
