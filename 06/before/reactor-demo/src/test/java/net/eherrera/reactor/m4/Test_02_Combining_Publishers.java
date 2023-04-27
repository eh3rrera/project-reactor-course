package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;

import java.util.Arrays;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_Combining_Publishers {
    @Test
    void example_01_merge() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);

        Flux<Integer> mergedFlux = Flux.merge(flux1, flux2);
        mergedFlux.subscribe(System.out::println);
    }

    @Test
    void example_02_mergeWith() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);

        Flux<Integer> mergedFlux = flux1.mergeWith(flux2);
        mergedFlux.subscribe(System.out::println);
    }

    @Test
    void example_03_mergeComparing() {
        Flux<Integer> flux1 = Flux.just(48, 45, 9);
        Flux<Integer> flux2 = Flux.just(26, 58, 2);

        Flux<Integer> mergedFlux = Flux.mergeComparing(flux1, flux2);
        mergedFlux.subscribe(System.out::println);
    }

    @Test
    void example_04_mergeSequential() {
        Flux<Integer> flux1 = Flux.just(10, 20, 30);
        Flux<Integer> flux2 = Flux.just(40, 50, 60);

        Flux<Integer> mergedFlux = Flux.mergeSequential(flux1, flux2);
        mergedFlux.subscribe(System.out::println);
    }

    @Test
    void example_05_concat() {
        Flux<Integer> flux1 = Flux.just(10, 20, 30);
        Flux<Integer> flux2 = Flux.just(40, 50, 60);

        Flux<Integer> concatFlux = Flux.concat(flux1, flux2);
        concatFlux.subscribe(System.out::println);
    }

    @Test
    void example_06_concatWith() {
        Flux<Integer> flux1 = Flux.just(10, 20, 30);
        Flux<Integer> flux2 = Flux.just(40, 50, 60);

        Flux<Integer> concatFlux = flux1.concatWith(flux2);
        concatFlux.subscribe(System.out::println);
    }

    @Test
    void example_07_zip() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);

        Flux<Tuple2<Integer, Integer>> zippedFlux = Flux.zip(flux1, flux2);
        zippedFlux.subscribe(System.out::println);
    }

    @Test
    void example_08_zip() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5);

        Flux<Tuple2<Integer, Integer>> zippedFlux = Flux.zip(flux1, flux2);
        zippedFlux.subscribe(System.out::println);
    }

    @Test
    void example_09_zip_BiFunction() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);

        Flux<Integer> zippedFlux = Flux.zip(flux1, flux2, (i1, i2) -> i1 + i2);
        zippedFlux.subscribe(System.out::println);
    }

    @Test
    void example_10_zip_Tuple4() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);
        Flux<Integer> flux3 = Flux.just(7, 8, 9);
        Flux<Integer> flux4 = Flux.just(10, 11, 12);

        Flux<Tuple4<Integer, Integer, Integer, Integer>> zippedFlux = Flux.zip(flux1, flux2, flux3, flux4);
        zippedFlux.subscribe(System.out::println);
    }

    @Test
    void example_11_zip_Object_array() {
        Flux<Integer> flux1 = Flux.just(1, 2, 3);
        Flux<Integer> flux2 = Flux.just(4, 5, 6);

        Flux<Integer> zippedFlux = Flux.zip(
                (Object[] elements) ->
                        Arrays.stream(elements) // Turn the array into a stream
                            .mapToInt(e -> (Integer)e) // Cast an element to Integer to create an IntStream
                            .sum() // Add up all the elements in the stream
                ,flux1, flux2);
        zippedFlux.subscribe(System.out::println);
    }

    @Test
    void example_12_zip_Function_Tuple() {
        Flux<Flux<Integer>> fluxOfFlux = Flux.just(
                Flux.just(1, 2, 3),
                Flux.just(4, 5, 6),
                Flux.just(7, 8, 9)
        );

        Flux<Integer> zippedFlux = Flux.zip(
                fluxOfFlux,
                (Tuple2 tuple) -> {
                    int total = 0;
                    for (int i = 0; i < tuple.size(); i++)
                        total += (Integer)tuple.get(i);
                    return total;
                }
        );
        zippedFlux.subscribe(System.out::println);
    }
}
