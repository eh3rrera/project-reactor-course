package net.eherrera.reactor.m7.exercises;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class Exercise03 {
    public static void main(String[] args) {
        Mono<String> greetingMono = Mono.just("Hello");
        String key = "username";

        // Append the value of the "username" key from the context to the emitted string
        Mono<String> contextualizedGreetingMono = greetingMono
                .transformDeferredContextual(
                        (original, ctx) ->
                                original.map(
                                        greeting ->
                                                greeting + ", " + ctx.get(key)
                                )
                );

        //  Add to the Context the value "Alice"
        //  Subscribe to contextualizedNumbers
        contextualizedGreetingMono
                .contextWrite(Context.of(key, "Alice"))
                .subscribe(System.out::println);

        //  Add to the Context the value "Bob"
        //  Subscribe again to contextualizedNumbers
        contextualizedGreetingMono
                .contextWrite(Context.of(key, "Bob"))
                .subscribe(System.out::println);
    }
}
