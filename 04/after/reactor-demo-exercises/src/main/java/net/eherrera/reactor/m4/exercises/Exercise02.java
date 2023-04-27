package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Exercise02 {
    public static void main(String[] args) throws InterruptedException {
        Flux<String> stockSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB")
                .delayElements(Duration.ofSeconds(1));

        // If a new symbol is emitted before the previous API call is completed,
        // use switchMap to cancel the previous call and switch to the new one.
        // Subscribe to Flux to print the latest stock price
        stockSymbols.switchMap(stockSymbol -> fetchLatestPrice(stockSymbol))
                .subscribe(System.out::println);

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
