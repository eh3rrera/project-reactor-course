package net.eherrera.reactor.m8.exercises;

import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public class Exercise02 {
    public static void main(String[] args) {
        // Generate a Flux from 1 to 10
        Flux<Integer> numberFlux = Flux.range(1, 10);

        // Transform the Flux into a lazy Stream blocking for each source onNext call
        Stream<Integer> numberStream = numberFlux.toStream();

        // Iterate over the elements of the Stream and print them
        numberStream.forEach(System.out::println);
    }
}
