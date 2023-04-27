package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;

public class Exercise06 {
    public static void main(String[] args) {
        Flux<Double> stockPrices = Flux.just(100.0, 200.0, 300.0, 400.0);

        // Calculate the total sum of all the prices
        stockPrices.reduce(0.0, (accumulator, price) -> accumulator + price)
                .subscribe(System.out::println);
    }
}
