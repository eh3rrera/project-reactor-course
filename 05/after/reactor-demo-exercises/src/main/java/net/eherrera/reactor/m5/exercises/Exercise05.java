package net.eherrera.reactor.m5.exercises;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

public class Exercise05 {
    public static void main(String[] args) {
        Flux<String> stringFlux = Flux.just("1", "2", "three", "4", "5");

        stringFlux.map(stringNumber -> {
                    try {
                        return Integer.parseInt(stringNumber);
                    } catch (NumberFormatException e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .subscribe(
                        System.out::println,
                        error -> System.out.println("Error: " + error.getMessage())
        );
    }
}
