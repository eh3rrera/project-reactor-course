package net.eherrera.reactor.m4.exercises;

import reactor.core.publisher.Flux;

public class Exercise03 {
    public static void main(String[] args) {
        Flux<String> stockSymbols = Flux.just("AAPL", "GOOG", "MSFT", "AMZN", "FB");
        Flux<String> marketNames = Flux.just("NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ", "NASDAQ");

        // TODO: Combine the elements from both publishers using this format: {STOCK} - {MARKET}
    }
}
