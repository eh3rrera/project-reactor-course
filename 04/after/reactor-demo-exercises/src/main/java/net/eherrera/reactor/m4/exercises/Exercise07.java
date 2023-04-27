package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;

public class Exercise07 {
    public static void main(String[] args) {
        Flux<Double> stockPrices = Flux.just(120.0, 140.0, 130.0, 110.0, 150.0);

        stockPrices.doFirst(() -> System.out.println("First stock price incoming..."))
                .doOnComplete(() -> System.out.println("All stock prices processed."))
                .doOnEach(signal -> {
                    if (signal.isOnNext()) {
                        System.out.println("Stock price: " + signal.get());
                    }
                })
                .doOnSubscribe(subscription -> System.out.println("Subscription started."))
                .subscribe();
    }
}
