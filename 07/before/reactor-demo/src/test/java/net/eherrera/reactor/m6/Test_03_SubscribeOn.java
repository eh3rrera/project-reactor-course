package net.eherrera.reactor.m6;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_04_ParallelFlux {
    @Test
    void example_01_parallel() {
        Flux.just(1, 2, 3, 4, 5)
                .parallel()
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName())
                );
    }

    @Test
    void example_02_runOn() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .parallel()
                .runOn(Schedulers.parallel())
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
        Thread.sleep(1000);
    }

    @Test
    void example_03_runOn() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .parallel()
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .runOn(Schedulers.parallel())
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
    void example_04_parallel() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .parallel(2)
                .runOn(Schedulers.parallel())
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
        Thread.sleep(1000);
    }

    @Test
    void example_05_sequential() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .sequential()
                .publishOn(Schedulers.boundedElastic())
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
    void example_06_groups() {
        Flux.just(1, 2, 3, 4, 5, 6)
                .parallel(2)
                //.runOn(Schedulers.parallel())
                .groups()
                .flatMap(group -> {
                    System.out.format("flatMap(%d) - %s\n",
                            group.key(),
                            Thread.currentThread().getName());
                    return group.collectList();
                })
                .subscribe(l -> System.out.format("subscribe(%s) - %s\n",
                        l.toString(),
                        Thread.currentThread().getName())
                );
    }
}
