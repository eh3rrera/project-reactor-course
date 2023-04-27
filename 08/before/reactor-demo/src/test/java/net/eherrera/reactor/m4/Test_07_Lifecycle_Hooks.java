package net.eherrera.reactor.m4;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_07_Lifecycle_Hooks {
    @Test
    void example_01_log() {
        Flux.just(1, 2, 3)
                .log()
                .subscribe(System.out::println);
    }

    @Test
    void example_02_doOnSucess() {
        Mono.just(1)
                .log()
                .doOnSuccess(i -> System.out.println("Mono completed successfully: " + i))
                .subscribe();
    }

    @Test
    void example_03_doOnComplete() {
        Flux.just(1, 2, 3)
                .log()
                .doOnComplete(() -> System.out.println("Flux completed successfully"))
                .subscribe();
    }

    @Test
    void example_04_lifecycleHooks() {
        Flux.just(1, 2, 3)
                .log()
                .doOnSubscribe(subscription -> System.out.println("doOnSubscribe: " + subscription))
                .doOnRequest(l -> System.out.println("doOnRequest: " + l))
                .doFirst(() -> System.out.println("doFirst"))
                .doOnNext(i -> System.out.println("doOnNext: " + i))
                .doOnEach(integerSignal -> System.out.println("doOnEach: " + integerSignal))
                .doFinally(signalType -> System.out.println("doFinally: " + signalType))
                .doAfterTerminate(() -> System.out.println("doAfterTerminate"))
                .doOnComplete(() -> System.out.println("doOnComplete"))
                .doOnTerminate(() -> System.out.println("doOnTerminate"))
                .subscribe();
    }
}
