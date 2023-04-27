package net.eherrera.reactor.m9;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_07_TestPublisher {
    @Test
    void example_01_HotFailing() {
        TestPublisher<Integer> testPublisher = TestPublisher.create();
        testPublisher.next(1);

        StepVerifier.create(testPublisher.flux())
                .expectNext(1)
                //.verifyComplete();
                .verifyTimeout(Duration.ofSeconds(1));
    }

    @Test
    void example_02_Cold() {
        TestPublisher<Integer> testPublisher = TestPublisher.createCold();
        testPublisher.next(1);

        StepVerifier.create(testPublisher.flux())
                .expectNext(1)
                //.verifyComplete();
                .verifyTimeout(Duration.ofSeconds(1));

        testPublisher.assertWasSubscribed();
        testPublisher.assertWasRequested();
    }

    @Test
    void example_03_Then() {
        TestPublisher<Integer> testPublisher = TestPublisher.create();

        StepVerifier.create(testPublisher.flux())
                .then(() -> {
                    testPublisher.assertWasSubscribed();
                    testPublisher.next(1);
                })
                .expectNext(1)
                .then(() -> testPublisher.complete())
                .verifyComplete();
        testPublisher.assertNoSubscribers();
    }

    @Test
    void example_04_Compliant() {
        TestPublisher<Integer> testPublisher = TestPublisher.create();

        StepVerifier.create(testPublisher.mono())
                .then(() -> {
                    testPublisher.next(1);
                })
                .expectNext(1)
                .then(testPublisher::complete)
                .then(testPublisher::complete)
                .verifyComplete();
    }

    @Test
    void example_05_NonCompliant() {
        TestPublisher<Integer> testPublisher = TestPublisher.createNoncompliant(TestPublisher.Violation.CLEANUP_ON_TERMINATE);

        StepVerifier.create(testPublisher.mono())
                .then(() -> {
                    testPublisher.next(1);
                })
                .expectNext(1)
                .then(testPublisher::complete)
                .then(testPublisher::complete)
                .verifyComplete();
    }
}
