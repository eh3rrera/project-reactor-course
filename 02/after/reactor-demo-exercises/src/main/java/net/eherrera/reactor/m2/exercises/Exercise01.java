package net.eherrera.reactor.m2.exercises;

import reactor.core.publisher.Mono;

import java.util.Optional;

public class Exercise01 {
    public static void main(String[] args) {
        // Create two Optional variables, one with a value and one empty
        Optional<String> optionalWithValue = Optional.of("Hi!");
        Optional<String> optionalEmpty = Optional.empty();

        // Use the justOrEmpty method to create Mono instances for each Optional
        Mono<String> monoWithValue = Mono.justOrEmpty(optionalWithValue);
        Mono<String> monoEmpty = Mono.justOrEmpty(optionalEmpty);

        // Subscribe to both Monos and print the results
        monoWithValue.subscribe(
                element -> System.out.println("Mono with value - Value: " + element),
                error -> System.err.println("Mono with value - Error: " + error.getMessage()),
                () -> System.out.println("Mono with value complete")
        );

        monoEmpty.subscribe(
                element -> System.out.println("Mono empty - Value: " + element),
                error -> System.err.println("Mono empty - Error: " + error.getMessage()),
                () -> System.out.println("Mono empty complete")
        );
    }
}
