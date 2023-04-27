package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_06_Aggregate_Flux {
    @Test
    void example_01_reduce() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .reduce(10, (a, b) -> {
                    int result = a + b;
                    System.out.format("[%d + %d] = %d\n", a, b, result);
                    return result;
                })
                .subscribe(System.out::println);
    }

    @Test
    void example_02_reduce() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .reduce((a, b) -> {
                    int result = a + b;
                    System.out.format("[%d + %d] = %d\n", a, b, result);
                    return result;
                })
                .subscribe(System.out::println);
    }

    @Test
    void example_03_reduceWith() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .reduceWith(() -> 10, (a, b) -> {
                    int result = a + b;
                    System.out.format("[%d + %d] = %d\n", a, b, result);
                    return result;
                })
                .subscribe(System.out::println);
    }

    @Test
    void example_04_scan() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .scan(10, (a, b) -> a + b)
                .subscribe(System.out::println);
    }

    @Test
    void example_05_scan() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .scan((a, b) -> a + b)
                .subscribe(System.out::println);
    }

    @Test
    void example_06_scanWith() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .scanWith(() -> 10, (a, b) -> a + b)
                .subscribe(System.out::println);
    }

    @Test
    void example_07_collectMap() {
        Flux<Integer> integerFlux = Flux.just(11, 22, 33, 34);
        Mono<Map<Integer, Integer>> monoMap = integerFlux
                .collectMap(i -> i / 10,
                            i -> i % 10
                );
        monoMap.subscribe(System.out::println);
    }

    @Test
    void example_08_collectMultimap() {
        Flux<Integer> integerFlux = Flux.just(11, 22, 33, 34);
        Mono<Map<Integer, Collection<Integer>>> monoMap =
                integerFlux.collectMultimap(
                        i -> i / 10,
                        i -> i % 10
                );
        monoMap.subscribe(System.out::println);
    }

    @Test
    void example_09_collectSortedList() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        Mono<List<Integer>> monoSortedList =
                integerFlux.collectSortedList(
                        Comparator.reverseOrder()
                );
        monoSortedList.subscribe(System.out::println);
    }

    @Test
    void example_10_hasElement() {
        Mono<Integer> integerMono = Mono.just(1);
        integerMono
                .filter(i -> i > 1)
                .hasElement()
                .subscribe(System.out::println);
    }

    @Test
    void example_11_hasElement() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .filter(i -> i > 2)
                .hasElement(3)
                .subscribe(System.out::println);
    }

    @Test
    void example_12_hasElements() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .filter(i -> i > 2)
                .hasElements()
                .subscribe(System.out::println);
    }

    @Test
    void example_13_all() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .all(i -> i % 2 == 0)
                .subscribe(System.out::println);
    }

    @Test
    void example_14_any() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3);
        integerFlux
                .any(i -> i % 2 == 0)
                .subscribe(System.out::println);
    }

    @Test
    void example_15_count() {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
        Mono<Long> integerMono = integerFlux.count();
        integerMono.subscribe(System.out::println);
    }
}
