---
layout: default
title: Coding Exercises
parent: Using Other Reactive Operators
nav_order: 8
---

# Exercises
---

In these exercises, you'll practice some of the concepts taught in this module.

First, either create a new Java project, adding the `reactor-bom` and `reactor-core` dependencies to your build file (Maven or Gradle) or use the stub you can find at: [https://github.com/eh3rrera/project-reactor-course/tree/main/04/before/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/04/before/reactor-demo-exercises).

I'll give you the instructions (and sometimes hints) so you can put all the code together in the `main` method of a class and observe the output.

Here you can find the solution for the coding exercises: [https://github.com/eh3rrera/project-reactor-course/tree/main/04/after/reactor-demo-exercises](https://github.com/eh3rrera/project-reactor-course/tree/main/04/after/reactor-demo-exercises).

----

## Exercise 1
You are given a list of stock symbols, and you need to fetch their historical price data from an external API. You want to keep the order of the received data. Using the [concatMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#concatMap-java.util.function.Function-) method, implement a solution that fetches the historical price data for each stock symbol in a sequential and non-interleaved manner.

1. Given the following code:
    ```java
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.time.Duration;
    import java.util.Arrays;
    import java.util.List;

    public class Exercise01 {

        public static void main(String[] args) throws InterruptedException {
            List<String> stockSymbols = Arrays.asList("AAPL", "GOOG", "MSFT", "AMZN", "FB");

            // TODO: Create a Flux from stockSymbols
            // TODO: Fetch historical prices
            // TODO: Subscribe to the Flux

            Thread.sleep(5500);
        }

        // Simulates an external API call to get historical stock prices
        public static Mono<List<Double>> fetchHistoricalPrices(String stockSymbol) {
            int c = stockSymbol.charAt(0);
            return Mono.just(Arrays.asList(c*10.0, c*20.0, c*30.0))
                    .delayElement(Duration.ofMillis(1000));
        }
    }
    ```
2. Create a `Flux` from the `stockSymbols` list.
3. Using the method `fetchHistoricalPrices` and the `concatMap` operator, fetch the historical prices for each stock.
4. Subscribe to the `Flux`, printing the emitted items.
6. Run the `Exercise01` class and analyze the output.

----

## Exercise 2
You are given a `Flux` of stock symbols that emits a new symbol every second. Your task is to fetch the latest stock price for each emitted symbol using an external API. However, you should only fetch the latest stock price for the most recent symbol emitted by the `Flux`. If a new symbol is emitted before the previous API call is completed, cancel the previous call and fetch the stock price for the new symbol. Implement a solution using the [switchMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#switchMap-java.util.function.Function-) operator.
1. Given the following code:
    ```java
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;

    import java.time.Duration;

    public class Exercise02 {
        public static void main(String[] args) throws InterruptedException {
            Flux<String> stockSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB")
                    .delayElements(Duration.ofSeconds(1));

            // TODO: If a new symbol is emitted before the previous API call is completed,
            // use switchMap to cancel the previous call and switch to the new one.
            // TODO: Subscribe to Flux to print the latest stock price

            Thread.sleep(15000);
        }

        // Simulates an external API call to get the latest stock price,
        // sometimes taking less than 1 second, other times taking more
        public static Mono<Double> fetchLatestPrice(String stockSymbol) {
            int c = stockSymbol.charAt(0);
            return Mono.just(c*10.0)
                    .delayElement(Duration.ofMillis(935+c));
        }
    }
   ```
2. Using the method `fetchLatestPrice` and the `switchMap` operator, fetch the latest price for each stock.
3. Subscribe to the `Flux`, printing the emitted items.
4. Run the `Exercise02` class and analyze the output. Are the prices of all stocks printed? Why or why not?

----

## Exercise 3
You are given two publishers, one that emits a list of stock symbols and another that emits their corresponding market names. Your task is to combine the elements from both publishers such that each stock symbol is paired with its respective market name. 
1. Given the following code:
    ```java
    public class Exercise03 {
        public static void main(String[] args) {
            Flux<String> stockSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB");
            Flux<String> marketNames = Flux.just("NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ");

            // TODO: Combine the elements from both publishers using this format: {stock} - {market}
        }
    }
   ```
2. Combine the elements from both publishers using this format: `{STOCK} - {MARKET}`.
3. Subscribe to the `Flux`, printing the emitted items.
4. Run the `Exercise03` class and analyze the output.

----

## Exercise 4
You are given a `Flux` that emits a list of stock prices. Your task is to filter the prices and only emit the prices that are greater than a given threshold. 
1. Given the following code:
    ```java
    public class Exercise04 {
        public static void main(String[] args) {
            Flux<Double> stockPrices = Flux.just(120.0, 140.0, 130.0, 110.0, 150.0);
            double threshold = 200.0;

            // TODO: If the stock price is greater than the threshold, emit it; otherwise, ignore it
            Flux<Double> filteredStockPrices = null;

            filteredStockPrices.subscribe(
                    value -> System.out.println("Filtered value: " + value),
                    error -> System.err.println("Error: " + error.getMessage()),
                    () -> System.out.println("Filtering completed")
            );
        }
    }
   ```
2. Filter the values of `stockPrices`. If the stock price is greater than the threshold, emit it. Otherwise, ignore it.
3. Subscribe to the `Flux`, printing the emitted items.
4. Run the `Exercise04` class and analyze the output. What's printed? Why?

----

## Exercise 5
You are given a `Flux` that emits a list of stock symbols. Your task is to check if the `Flux` is empty or not. If it is empty, use an alternative `Flux` containing a default list of stock symbols.
1. Given the following code:
    ```java
    import reactor.core.publisher.Flux;

    public class Exercise05 {

        public static void main(String[] args) {
            Flux<String> stockSymbols = Flux.empty(); // Empty Flux
            Flux<String> defaultSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB");

            // TODO: Check if stockSymbols is empty or not. If it is empty, use defaultSymbols
        }
    }
   ```
2. Utilizing the appropriate operator, use `defaultSymbols` if `stockSymbols` is empty (which it is, but assume we don't know it).
3. Subscribe to the `Flux`, printing the emitted items.
4. Run the `Exercise05` class and analyze the output.

----

## Exercise 6
You are given a `Flux` that emits a list of stock prices. Your task is to calculate the total sum of all the prices using the appropriate operator.
1. Given the following code:
    ```java
    import reactor.core.publisher.Flux;

    public class Exercise06 {

        public static void main(String[] args) {
            Flux<Double> stockPrices = Flux.just(100.0, 200.0, 300.0, 400.0);

            // TODO: Calculate the total sum of all the prices
        }
    }
   ```
2. Utilizing the appropriate operator, calculate the total sum of all the prices.
3. Subscribe to the `Flux`, printing the emitted item (the total).
4. Run the `Exercise06` class and analyze the output.

----

## Exercise 7
You are given a `Flux` that emits a list of stock prices:
```java
import reactor.core.publisher.Flux;

public class Exercise07 {
    public static void main(String[] args) {
        Flux<Double> stockPrices = Flux.just(120.0, 140.0, 130.0, 110.0, 150.0);

        // TODO: Your implementation goes here
    }
}
```
Your task is to implement the following requirements using hooks:
1. When the `Flux` starts emitting, print `"First stock price incoming..."`
2. When the `Flux` completes, print `"All stock prices processed."`
3. Print each price in the `Flux`.
4. When the `Flux` is subscribed, print `"Subscription started."`