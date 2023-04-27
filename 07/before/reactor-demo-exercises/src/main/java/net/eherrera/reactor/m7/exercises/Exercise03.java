package net.eherrera.reactor.m7.exercises;

import reactor.core.publisher.Mono;

public class Exercise03 {
    public static void main(String[] args) {
        Mono<String> greetingMono = Mono.just("Hello");
        String key = "username";

        // TODO: Append the value of the "username" key from the context to the emitted string
        Mono<String> contextualizedGreetingMono = null;

        //  TODO: Add to the Context the value "Alice"
        //  TODO: Subscribe to contextualizedNumbers

        //  TODO: Add to the Context the value "Bob"
        //  TODO: Subscribe again to contextualizedNumbers
    }
}
