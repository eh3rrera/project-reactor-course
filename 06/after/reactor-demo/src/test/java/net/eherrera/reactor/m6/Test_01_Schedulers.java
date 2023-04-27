package net.eherrera.reactor.m6;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_Schedulers {
    @Test
    void example_01_Thread() {
        Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return Mono.just(i * 10);
                })
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                                                    i,
                                                    Thread.currentThread().getName())
                );
    }

    @Test
    void example_02_Thread() throws InterruptedException {
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return Mono.just(i * 10);
                });
        Thread myThread = new Thread(() ->
                integerFlux
                        .subscribe(i ->
                                System.out.format("subscribe(%d) - %s\n",
                                            i,
                                            Thread.currentThread().getName())
                        )
        );
        myThread.start();
        myThread.join(); // So the program can wait for this thread to finish
    }

    @Test
    void example_03_Thread() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .delayElements(Duration.ofMillis(10))
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return Mono.just(i * 10);
                })
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName())
                );
        Thread.sleep(1000);
    }

    @Test
    void example_04_CustomScheduler() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .flatMap(i -> {
                    System.out.format("flatMap(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return Mono.just(i * 10);
                })
                .delaySubscription(Duration.ofMillis(10), Schedulers.boundedElastic())
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName())
                );
        Thread.sleep(1000);
    }
}
