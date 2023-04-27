package net.eherrera.reactor.m6.exercises;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Exercise01 {
    public static void main(String[] args) throws InterruptedException {
        Flux<String> urlFlux = Flux.just("url1", "url2", "url3", "url4");

        // Change the threading context and apply the fetchAndCountWords method
        Flux<Integer> wordCountFlux = urlFlux
                .publishOn(Schedulers.boundedElastic())
                .map(url -> fetchAndCountWords(url));

        // Subscribe to the Flux and print the emitted element
        wordCountFlux.subscribe(wordCount -> System.out.println("Word count: " + wordCount));

        Thread.sleep(4000);
    }

    // Simulates fetching the content of a URL and counting the number of words
    public static int fetchAndCountWords(String url) {
        int random = (int)(Math.random() * 500 + 100);
        System.out.println("Word count for " + url + ": " + random);
        return random;
    }
}
