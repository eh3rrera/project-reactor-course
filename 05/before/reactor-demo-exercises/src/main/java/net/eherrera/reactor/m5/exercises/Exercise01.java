package net.eherrera.reactor.m5.exercises;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Exercise01 {
    public static void main(String[] args) {
        Flux<List<String>> userNamesFlux = Flux.defer(() -> {
            // TODO Create either a Flux with the result or with an error
            return null;
        });

        userNamesFlux.subscribe(userNames -> System.out.println("User data: " + userNames),
                error -> System.out.println("Error fetching user data: " + error.getMessage()));
    }

    public static List<String> simulateApiCall() {
        List<String> userNames = Arrays.asList("Alice", "Bob", "Carol", "David");
        Random random = new Random();
        if (random.nextBoolean()) {
            throw new RuntimeException("Remote API error");
        }
        return userNames;
    }
}
