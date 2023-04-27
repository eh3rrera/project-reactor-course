package net.eherrera.reactor.m8;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_HandlingBlockingCalls {
    @Test
    void example_01_WrongWay() {
        Mono.fromSupplier(() -> blockingMethod())
                .subscribe(System.out::println);
    }

    @Test
    void example_02_RightWay() {
        Mono.fromCallable(() -> blockingMethod())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(System.out::println);
    }

    @Test
    void example_03_ThreadNames() throws InterruptedException {
        Mono.fromCallable(() -> {
                    System.out.println("fromCallable: " + Thread.currentThread().getName());
                    return blockingMethod();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    System.out.println("map: " + Thread.currentThread().getName());
                    return s;
                })
                .subscribe(System.out::println);
        // To give time to the program to execute
        Thread.sleep(500);
    }

    @Test
    void example_04_ThreadNames() throws InterruptedException {
        Mono<String> mono = Mono.fromCallable(() -> {
                    System.out.println("fromCallable: " + Thread.currentThread().getName());
                    return blockingMethodWithSleep();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    System.out.println("map: " + Thread.currentThread().getName());
                    return s;
                });

        for(int i = 0; i < 5; i++) {
            new Thread(() -> mono.subscribe(System.out::println)).start();
        }
        // To give time to the program to execute
        Thread.sleep(5000);
    }

    String blockingMethod() {
        String data = null;
        try {
            Path path = Paths.get(
                    getClass()
                            .getClassLoader()
                            .getResource("1.txt")
                            .toURI()
            );

            try(Stream<String> lines = Files.lines(path)) {
                data = lines.collect(Collectors.joining("\n"));
            }
        } catch(Exception e) {
            e.printStackTrace();
            data = "0";
        }
        return data;
    }

    String blockingMethodWithSleep() {
        String data = null;
        try {
            Path path = Paths.get(
                    getClass()
                            .getClassLoader()
                            .getResource("1.txt")
                            .toURI()
            );

            Thread.sleep(1000);

            try(Stream<String> lines = Files.lines(path)) {
                data = lines.collect(Collectors.joining("\n"));
            }
        } catch(Exception e) {
            e.printStackTrace();
            data = "0";
        }
        return data;
    }
}
