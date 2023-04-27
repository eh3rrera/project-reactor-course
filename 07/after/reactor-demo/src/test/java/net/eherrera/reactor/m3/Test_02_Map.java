package net.eherrera.reactor.m3;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_02_Map {
    Function<String, LocalDate> stringToDateFunction =
            s -> LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));

    @Test
    void example_01_MonoMap() {
        Mono<String> monoString = Mono.just("2022-01-01");
        Mono<LocalDate> monoDate = monoString.map(stringToDateFunction);

        monoDate.subscribe(d -> System.out.println(
                d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
    }

    @Test
    void example_02_FluxMap() {
        Flux<String> fluxString = Flux.just("2022-01-02", "2022-01-03", "2022-01-04");
        Flux<LocalDate> fluxDate = fluxString.map(stringToDateFunction);

        fluxDate.subscribe(d -> System.out.println(
                d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
    }

    @Test
    void example_03_ImperativeMap() {
        Function<String, LocalDate> stringToDateImperativeFunction = s -> {
            if(s != null && s.contains("-")) {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
            } else {
                return LocalDate.now();
            }
        };

        Mono<String> monoString = Mono.just("2022-01-05");
        Mono<LocalDate> monoDate = monoString.map(stringToDateImperativeFunction);

        monoDate.subscribe(d -> System.out.println(
                d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
    }

    @Test
    void example_04_IntegerMap() {
        Mono<Integer> monoInteger = Mono.just(1)
                .map(i -> i * 2);

        monoInteger.subscribe(System.out::println);
    }

    @Test
    void example_05_ListMap() {
        Mono<List<Integer>> monoListInteger = Mono.just(1)
                .map(i -> Arrays.asList(1));

        monoListInteger.subscribe(System.out::println);
    }

    @Test
    void example_06_AsyncMap() {
        Mono.just(1)
                .map(i -> asyncTransformation(i))
                //.map(j -> j * 10)  // Compiler error, the type of j is Mono<Integer>
        ;
    }
    Mono<Integer> asyncTransformation(int i) {
        // Modify i in some way
        return Mono.just(i);
    }
}
