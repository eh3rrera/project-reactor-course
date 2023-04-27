package net.eherrera.reactor.m7.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Exercise02 {
    public static void main(String[] args) {
        Flux<Integer> numbers = Flux.range(1, 5);
        String key = "multiplier";
        int value = 3;
        int defaultValue = 1;

        // Multiply each emitted number by the "multiplier" value from the context
        Flux<Integer> contextualizedNumbers = numbers
                .flatMap(i -> Mono.deferContextual(
                            ctx -> Mono.just(i * ctx.getOrDefault(key, defaultValue))
                        )
                );

        //  Add to the Context the key-value pair with the variables of the same name
        //  Subscribe to contextualizedNumbers
        contextualizedNumbers
                .contextWrite(ctx -> ctx.put(key, value))
                .subscribe(System.out::println);
    }
}
