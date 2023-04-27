package net.eherrera.reactor.m5;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_03_CheckedExceptions {
    @Test
    void example_01_error() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .filter(i -> i > 10)
                .switchIfEmpty(Flux.error(new Exception("List must not be empty")))
                .subscribe(System.out::println);
    }

    @Test
    void example_02_CatchCheckedException() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, -4, 5);
        integerFlux
                //.map(i -> getValue(i))  // This won't compile
                .map(i -> {
                    try {
                        return getValue(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                })
                .subscribe(System.out::println);
    }

    @Test
    void example_03_bubble() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, -4, 5);
        integerFlux
                //.map(i -> getValue(i))  // This won't compile
                .map(i -> {
                    try {
                        return getValue(i);
                    } catch (Exception e) {
                        throw Exceptions.bubble(e);
                    }
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_04_propagate() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, -4, 5);
        integerFlux
                //.map(i -> getValue(i))  // This won't compile
                .map(i -> {
                    try {
                        return getValue(i);
                    } catch (Exception e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .subscribe(System.out::println,
                        System.out::println
                );
    }

    @Test
    void example_05_unwrap() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, -4, 5);
        integerFlux
                //.map(i -> getValue(i))  // This won't compile
                .map(i -> {
                    try {
                        return getValue(i);
                    } catch (Exception e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .subscribe(System.out::println,
                        e -> System.out.println(Exceptions.unwrap(e))
                );
    }

    private int getValue(int i) throws Exception {
        if(i < 0) {
            throw new Exception("The input value cannot be zero");
        }
        return i * 10;
    }
}
