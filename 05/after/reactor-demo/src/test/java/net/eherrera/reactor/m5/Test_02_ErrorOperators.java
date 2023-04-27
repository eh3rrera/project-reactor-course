package net.eherrera.reactor.m5;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_ErrorOperators {
    @Test
    void example_01_doOnError() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .doOnError(ArithmeticException.class,
                        e -> System.out.println("ArithmeticException: " + e.getMessage())
                )
                .subscribe(System.out::println);
    }

    @Test
    void example_02_doOnError() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .doOnError(ArithmeticException.class,
                        e -> System.out.println("doOnError: " + e.getMessage())
                )
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_03_doFinally() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .doOnError(ArithmeticException.class,
                        e -> System.out.println("doOnError: " + e.getMessage())
                )
                .doFinally(signalType -> System.out.println("doFinally: " + signalType))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_04_onErrorReturn() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorReturn(ArithmeticException.class, 0)
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_05_onErrorReturn() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorReturn(e -> e.getMessage().contains("3"),
                        0)
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_06_onErrorResume() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorResume(e -> Flux.just(4, 5))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_07_onErrorResume() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .onErrorResume(e -> Flux.just(4, 5))
                .map(i -> i/(i-3))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_08_onErrorResume() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorResume(e -> Flux.error(new RuntimeException("Unexpected exception", e)))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_09_onErrorMap() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorMap(e -> new RuntimeException("Unexpected exception", e))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_10_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_11_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                    throw new RuntimeException(e);
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_12_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorResume(e -> Mono.just(99))
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_13_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .map(i -> i/(i-3))
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .onErrorResume(e -> Mono.just(99))
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_14_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .flatMap(val ->
                    Mono.just(val)
                            .map(i -> i/(i-3))
                            .doOnError(e ->System.out.println("Inside exception: " + e))
                            .onErrorResume(e -> Mono.empty())
                )
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_15_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .flatMap(val ->
                        Mono.just(val)
                                .map(i -> i/(i-3))
                                .doOnError(e ->System.out.println("Inside exception: " + e))
                                .onErrorResume(e -> Mono.empty())
                )
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_16_onErrorContinue() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .flatMap(val ->
                        Mono.just(val)
                                .map(i -> i/(i-3))
                                .doOnError(e ->System.out.println("Inside exception: " + e))
                                .onErrorResume(e -> Mono.empty())
                )
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_17_onErrorStop() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .flatMap(val ->
                        Mono.just(val)
                                .map(i -> i/(i-3))
                                .doOnError(e ->System.out.println("Inside exception: " + e))
                                .onErrorResume(e -> Mono.empty())
                                .onErrorStop()
                )
                .onErrorContinue((e, i) -> {
                    System.out.format("The value %d caused the exception: %s\n", i, e);
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }
}
