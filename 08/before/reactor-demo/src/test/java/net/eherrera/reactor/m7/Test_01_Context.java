package net.eherrera.reactor.m7;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_Context {
    @Test
    void example_01_TransformDeferredContextual() {
        String key = "multiplier";
        Flux<Integer> fluxInteger = Flux.just(1, 2, 3, 4, 5)
                .map(i -> i * 10)
                .transformDeferredContextual(
                        (flux, ctx) ->
                           flux.map(i -> i * ctx.getOrDefault(key, 1))
                );

        fluxInteger
                .contextWrite(Context.of(key, 10))
                .subscribe(System.out::println);
        fluxInteger
                .contextWrite(Context.of(key, 100))
                .subscribe(System.out::println);
    }

    @Test
    void example_02_PublishOn() {
        String key = "multiplier";
        Flux<Integer> fluxInteger = Flux.just(1, 2, 3, 4, 5)
                .map(i -> i * 10)
                .publishOn(Schedulers.newParallel("parallel"))
                .transformDeferredContextual(
                        (flux, ctx) ->
                                flux.map(i -> i * ctx.getOrDefault(key, 1))
                );

        fluxInteger
                .contextWrite(Context.of(key, 10))
                .subscribe(System.out::println);
    }

    @Test
    void example_03_DeferContextual() {
        String key = "multiplier";
        Flux<Integer> fluxInteger = Flux.just(1, 2, 3, 4, 5)
                .flatMap(i -> Mono.deferContextual(ctx ->
                            Mono.just(i * ctx.getOrDefault(key, 1))
                        )
                )
                .contextWrite(Context.of(key, 10));

        fluxInteger.subscribe(System.out::println);
    }

    @Test
    void example_04_ContextPlacement() {
        String key = "multiplier";
        Mono<Integer> monoInteger = Mono.just(1)
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap1: " + ctx);
                                    return Mono.just(i);
                                }
                        )
                )
                .contextWrite(Context.of(key, 10))
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap2: " + ctx);
                                    return Mono.just(i);
                                }
                        )
                );

        monoInteger.subscribe();
    }

    @Test
    void example_05_ContextPlacement() {
        String key = "multiplier";
        Mono<Integer> monoInteger = Mono.just(1)
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println(ctx);
                                    return Mono.just(i);
                                }
                        )
                )
                .contextWrite(Context.of(key, 100))
                .contextWrite(Context.of(key, 10));

        monoInteger.subscribe();
    }

    @Test
    void example_06_ContextPlacement() {
        String key = "multiplier";
        Mono<Integer> monoInteger = Mono.just(1)
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap1: " +ctx);
                                    return Mono.just(i);
                                }
                        )
                )
                .contextWrite(Context.of(key, 100))
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap2: " + ctx);
                                    return Mono.just(i);
                                }
                        )
                )
                .contextWrite(Context.of(key, 10));

        monoInteger.subscribe();
    }

    @Test
    void example_07_ContextPlacement() {
        String key = "multiplier";
        Mono<Integer> monoInteger = Mono.just(1)
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap(main sequence): " +ctx);
                                    return Mono.just(i);
                                }
                        )
                )
                .flatMap(i -> Mono.deferContextual(ctx -> {
                                    System.out.println("flatMap(inner Context): " + ctx);
                                    return Mono.just(i);
                                }
                        )
                        .contextWrite(Context.of(key, 100))
                )
                .contextWrite(Context.of(key, 10));

        monoInteger.subscribe();
    }
}
