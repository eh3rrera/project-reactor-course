package net.eherrera.reactor.m5.exercises;

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
