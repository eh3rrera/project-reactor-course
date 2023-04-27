package net.eherrera.reactor.m6;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_PublishOn {
    @Test
    void example_01_publishOn() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .publishOn(Schedulers.newSingle("singleScheduler"))
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
    void example_02_publishOn() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .publishOn(Schedulers.newParallel("parallelScheduler"))
                .map(i -> {
                    System.out.format("map(%d) - %s\n",
                            i,
                            Thread.currentThread().getName());
                    return i * 10;
                })
                .publishOn(Schedulers.newSingle("singleScheduler"))
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
    void example_03_publishOn() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .publishOn(Schedulers.newParallel("parallelScheduler"))
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
                .publishOn(Schedulers.newSingle("singleScheduler"))
                .subscribe(i -> System.out.format("subscribe(%d) - %s\n",
                        i,
                        Thread.currentThread().getName())
                );
        Thread.sleep(1000);
    }
}
