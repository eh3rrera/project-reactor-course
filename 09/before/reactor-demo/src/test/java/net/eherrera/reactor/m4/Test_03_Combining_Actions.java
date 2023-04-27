package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_03_Combining_Actions {
    @Test
    void example_01_then() {
        Mono<Integer> monoInt = Mono.fromCallable(() -> {
            System.out.println("Executing from monoInt...");
            return 1;
        });

        monoInt.then().subscribe(System.out::println);
    }

    @Test
    void example_02_then() {
        Mono<Integer> monoInt = Mono.fromCallable(() -> {
            System.out.println("Executing from monoInt...");
            return 1;
        });
        Mono<String> monoString = Mono.fromCallable(() -> {
            System.out.println("Executing from monoString...");
            return "a";
        });

        monoInt.then(monoString).subscribe(System.out::println);
    }

    @Test
    void example_03_thenEmpty() {
        Mono<Integer> monoInt = Mono.fromCallable(() -> {
            System.out.println("Executing from monoInt...");
            return 1;
        });
        Mono<String> monoString = Mono.fromCallable(() -> {
            System.out.println("Executing from monoString...");
            return "a";
        });
        Mono<Void> monoVoid = Mono.fromRunnable(() -> {
            System.out.println("Executing from monoVoid...");
        });

        monoInt.then(monoString).thenEmpty(monoVoid).subscribe(System.out::println);
    }

    @Test
    void example_04_thenMany() {
        Mono<Integer> monoInt = Mono.fromCallable(() -> {
            System.out.println("Executing from monoInt...");
            return 1;
        });
        Flux<Double> fluxDouble = Flux.just(1.2, 1.3);

        monoInt.thenMany(fluxDouble).subscribe(System.out::println);
    }

    @Test
    void example_05_thenReturn() {
        Mono<Integer> monoInt = Mono.fromCallable(() -> {
            System.out.println("Executing from monoInt...");
            return 1;
        });
        Mono<String> monoString = monoInt.thenReturn("a");

        monoString.subscribe(System.out::println);
    }
}
