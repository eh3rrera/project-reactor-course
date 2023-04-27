package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;

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
