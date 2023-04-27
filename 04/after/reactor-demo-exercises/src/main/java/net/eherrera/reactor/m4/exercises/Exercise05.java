package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;

public class Exercise05 {
    public static void main(String[] args) {
        Flux<String> stockSymbols = Flux.empty(); // Empty Flux
        Flux<String> defaultSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB");

        // Check if stockSymbols is empty or not. If it is empty, use defaultSymbols
        stockSymbols.switchIfEmpty(defaultSymbols)
                .subscribe(System.out::println);
    }
}
