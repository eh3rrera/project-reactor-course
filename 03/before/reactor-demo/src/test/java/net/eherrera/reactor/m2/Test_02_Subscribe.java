package net.eherrera.reactor.m2;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_Subscribe {
    @Test
    void example_01_Subscribe() {
        Mono.just(1).subscribe();
    }

    @Test
    void example_02_Subscribe() {
        Mono.just(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });
        // As a lambda expression
        Mono.just(1).subscribe(integer -> System.out.println(integer));
        // As a method reference
        Mono.just(1).subscribe(System.out::println);
    }

    @Test
    void example_03_Subscribe() {
        Mono.defer(() -> Mono.just(getException()))
                .subscribe(
                        System.out::println,
                        e -> System.out.println("Message: " + e.getMessage())
                );
    }
    private Integer getException() {
        return 1/0;
    }

    @Test
    void example_04_Subscribe() {
        Mono.just(1)
                .subscribe(
                        null,
                        null,
                        () -> System.out.println("The end")
                );
    }

    @Test
    void example_05_Subscribe() {
        Flux.just(1, 2, 3)
                .subscribe( // It's marked as deprecated
                        System.out::println,
                        null,
                        null,
                        s -> s.request(1)
                );
    }

    @Test
    void example_06_Subscribe() {
        Flux.just(1, 2, 3)
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription s) {
                        s.request(1L);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println(value);
                    }
                });
    }
}
