package net.eherrera.reactor.m7.exercises;

import reactor.core.publisher.Flux;
import reactor.util.context.Context;

public class Exercise01 {
    public static void main(String[] args) {
        Flux<Integer> numbers = Flux.range(1, 5);
        String key = "divider";
        double value = 10.0;
        double defaultValue = 1.0;

        // Divide each emitted number by the "divider" value from the context
        Flux<Double> contextualizedNumbers = numbers
                .transformDeferredContextual(
                        (flux, ctx) ->
                                flux.map(
                                        i -> i / ctx.getOrDefault(key, defaultValue)
                                )
                );

        //  Add to the Context the key-value pair with the variables of the same name
        //  Subscribe to contextualizedNumbers
        contextualizedNumbers
                .contextWrite(Context.of(key, value))
                .subscribe(System.out::println);
    }
}
