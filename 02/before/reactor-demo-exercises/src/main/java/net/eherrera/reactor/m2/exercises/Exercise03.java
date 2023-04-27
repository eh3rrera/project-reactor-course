package net.eherrera.reactor.m2.exercises;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import reactor.core.publisher.Mono;

public class Exercise03 {
    public static void main(String[] args) {
        // Create a simple CompletableFuture
        CompletableFuture<String> future = createCompletableFuture();

        // TODO: Convert the CompletableFuture to a Mono using fromFuture()

        // TODO: Subscribe to the Mono
    }

    private static CompletableFuture<String> createCompletableFuture() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> "Hi!", executor);
    }
}
