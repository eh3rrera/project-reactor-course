package net.eherrera.reactor.m5.exercises;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Exercise03 {
    public static void main(String[] args) {
        String fallbackUrl = "fallbackUrl";

        Flux.just("url1", "url2", "url3", "url4")
                .flatMap(url -> Mono.fromCallable(() -> fetchData(url))
                        .map(jsonString -> parseJson(jsonString))
                        .onErrorResume(RuntimeException.class, e -> {
                            String fallbackJson = fetchData(fallbackUrl);
                            return Mono.just(parseJson(fallbackJson));
                        }))
                .subscribe(System.out::println);
    }

    public static String fetchData(String url) {
        // Simulates fetching JSON data from the URL
        return "{\"data\": \"" + url + "\"}";
    }

    public static String parseJson(String jsonString) {
        // Simulates the parsing of jsonString and throwing an exception if it is invalid
        String str = "";
        if(jsonString.contains("url3"))
            throw new RuntimeException("Invalid JSON");
        else
            str = jsonString.toUpperCase();
        return str;
    }
}
