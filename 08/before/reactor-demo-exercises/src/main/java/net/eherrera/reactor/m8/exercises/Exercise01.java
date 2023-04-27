package net.eherrera.reactor.m8.exercises;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.Optional;

public class Exercise01 {
    public static void main(String[] args) {
        String url = "https://example.com";

        // TODO: Create a Mono from the fetchUrlContent method

        // Set the timeout duration
        Duration timeout = Duration.ofSeconds(5);

        // TODO: Get an Optional using the specified timeout
        Optional<String> contentOptional = null;

        // Handle the result of the request
        contentOptional.ifPresentOrElse(
                content -> System.out.println("URL content: " + content),
                () -> System.out.println("The Mono completed empty.")
        );
    }

    public static Mono<String> fetchUrlContent(String url) {
        HttpClient httpClient = HttpClient.create();
        return httpClient.get()
                .uri(url)
                .responseSingle((response, content) ->
                        content.asString()
                );
    }
}
