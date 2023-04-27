package net.eherrera.reactor.m2;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_CreatingSequences {
    @Test
    void example_01_Empty() {
        Mono<String> emptyMono = Mono.empty();
        Flux<String> emptyFlux = Flux.empty();
    }

    @Test
    void example_02_Just() {
        Mono<Integer> integerMono = Mono.just(1);
        Flux<Integer> integerFlux = Flux.just(1, 2);
        //Mono<Integer> integerMono = Mono.just(1, 2); // Compiler error
    }

    @Test
    void example_03_Just() {
        Flux<Integer> integerFlux = Flux.just(); // No error
        //Mono<Integer> integerMono = Mono.just(); // Error
    }

    @Test
    void example_04_JustOrEmpty() {
        Mono<Integer> emptyMono1 = Mono.justOrEmpty(Optional.empty());
        Mono<Integer> emptyMono2 = Mono.justOrEmpty(null);
    }

    @Test
    void example_05_From() {
        Flux<Integer> integerFlux = Flux.just(1, 2);
        Mono<Integer> mono1 = Mono.from(integerFlux);
        Mono<Integer> mono2 = Mono.fromDirect(integerFlux);
    }

    private int myValue = 0;
    @Test
    void example_06_FromRunnable() {
        Mono<Void> runnableMono = Mono.fromRunnable(new Runnable() {
            @Override
            public void run() {
                myValue++;
            }
        });
        // Using a lambda expression to simplify the code
        Mono<Void> runnableMono2 = Mono.fromRunnable(
                () -> System.out.println("Hello from Runnable!")
        );
    }

    @Test
    void example_07_FromFutureEager() {
        Mono<String> futureMonoEager = Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            System.out.println("Eager");
            return "Hello from eager future!";
        }));
    }

    @Test
    void example_08_FromFutureLazy() {
        Mono<String> futureMonoLazy = Mono.fromFuture(() -> CompletableFuture.supplyAsync(() -> {
            System.out.println("Lazy");
            return "Hello from lazy future!";
        }));
    }

    @Test
    void example_09_FromIterable() {
        List<Integer> myList = Arrays.asList(1, 2, 3);
        Flux listFlux = Flux.fromIterable(myList);
    }

    @Test
    void example_10_FromStreamOneTime() {
        Stream stream = Stream.of(1, 2, 3);
        Flux<Integer> streamFluxUseOneTime = Flux.fromStream(stream);
    }

    @Test
    void example_11_FromStreamMultipleTimes() {
        Flux<Integer> streamFluxUseMultipleTimes = Flux.fromStream(() -> Stream.of(1, 2, 3));
    }

    @Test
    void example_12_Defer() {
        Flux<Integer> fluxDeferred = Flux.defer( () -> Flux.just(1, 2, 3));
    }

    @Test
    void example_13_Defer() {
        // getValue() can be executed multiple times
        Mono<Integer> monoDeferred = Mono.defer(() -> Mono.just(getValue()));
        // getValue() will be executed only once
        Mono<Integer> monoNotDeferred = Mono.just(getValue());
    }
    private Integer getValue() {
        System.out.println("getValue()");
        return 1;
    }


}
