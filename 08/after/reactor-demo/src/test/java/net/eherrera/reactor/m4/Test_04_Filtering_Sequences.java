package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_04_Filtering_Sequences {
    @Test
    void example_01_filter() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        Flux<Integer> filteredFlux = integerFlux.filter(i -> i % 2 == 0);

        filteredFlux.subscribe(System.out::println);
    }

    @Test
    void example_02_filter() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        Flux<Integer> filteredFlux = integerFlux.filter(i -> i > 100);

        filteredFlux.subscribe(System.out::println);
    }

    @Test
    void example_03_filterWhen() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        integerFlux
                .filterWhen(i -> Mono.just(i % 2 == 0))
                .subscribe(System.out::println);
    }

    @Test
    void example_04_filterWhen() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        integerFlux
                .filterWhen(i -> Mono.just(false))
                //.filterWhen(i -> Mono.empty()) // Same as false
                .subscribe(System.out::println);
    }

    @Test
    void example_05_filterWhen() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        integerFlux
                .filterWhen(i -> Flux.just(i < 6, i % 2 == 0))
                .subscribe(System.out::println);
    }

    @Test
    void example_06_distinct() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 1, 4, 2);
        integerFlux
                .distinct()
                .subscribe(System.out::println);
    }

    @Test
    void example_07_distinctUntilChanged() {
        Flux<Integer> integerFlux = Flux.just(1, 1, 1, 2, 3, 3, 4, 2);
        integerFlux
                .distinctUntilChanged()
                .subscribe(System.out::println);
    }

    @Test
    void example_08_take() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .take(2)
                .subscribe(System.out::println);
    }

    @Test
    void example_09_take() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .delayElements(Duration.ofMillis(1))
                .take(Duration.ofMillis(10))
                .subscribe(System.out::println);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void example_10_takeUntilOther() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .delayElements(Duration.ofMillis(10))
                .takeUntilOther(Mono.just(10).delayElement(Duration.ofMillis(5)))
                .subscribe(System.out::println);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void example_11_takeUntil() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .takeUntil(i -> i == 3)
                .subscribe(System.out::println);
    }

    @Test
    void example_12_takeWhile() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .takeWhile(i -> i < 4)
                .subscribe(System.out::println);
    }

    @Test
    void example_13_skip() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .skip(2)
                .subscribe(System.out::println);
    }

    @Test
    void example_14_skip() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .delayElements(Duration.ofMillis(1))
                .skip(Duration.ofMillis(5))
                .subscribe(System.out::println);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void example_15_skipUntilOther() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .delayElements(Duration.ofMillis(10))
                .skipUntilOther(Mono.just(10).delayElement(Duration.ofMillis(5)))
                .subscribe(System.out::println);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void example_16_skipUntil() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .skipUntil(i -> i == 3)
                .subscribe(System.out::println);
    }

    @Test
    void example_17_skipWhile() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        integerFlux
                .skipWhile(i -> i < 4)
                .subscribe(System.out::println);
    }
}
