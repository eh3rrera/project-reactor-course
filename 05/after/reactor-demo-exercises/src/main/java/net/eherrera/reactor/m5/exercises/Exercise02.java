package net.eherrera.reactor.m5.exercises;

import reactor.core.publisher.Flux;

public class Exercise02 {
    public static void main(String[] args) {
        Flux.just(1, 2, -3, 4)
                .map(num -> {
                    if (num < 0) {
                        throw new IllegalArgumentException("Negative numbers are not allowed.");
                    }
                    return num * 2;
                })
                .doOnError(IllegalArgumentException.class, e -> System.out.println("Error: " + e.getMessage()))
                .doFinally(signal -> System.out.println("Finished processing the list."))
                .subscribe(System.out::println, error -> { });
    }
}
