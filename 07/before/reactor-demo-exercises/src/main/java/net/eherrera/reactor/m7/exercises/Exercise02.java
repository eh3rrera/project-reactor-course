package net.eherrera.reactor.m7.exercises;

import reactor.core.publisher.Flux;

public class Exercise02 {
    public static void main(String[] args) {
        Flux<Integer> numbers = Flux.range(1, 5);
        String key = "multiplier";
        int value = 3;
        int defaultValue = 1;

        // TODO: Multiply each emitted number by the "multiplier" value from the context
        Flux<Integer> contextualizedNumbers = null;

        //  TODO: Add to the Context the key-value pair with the variables of the same name
        //  TODO: Subscribe to contextualizedNumbers
    }
}
