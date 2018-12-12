package com.turkcell.reactive.meetup.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Created by Sercan CELENK at 2018-12-06
 */

@Service
@Slf4j
public class ReactiveWorkshopService {
    public Flux<String> flatMap(int from) {
        return countDown(from)
                .flatMap(i -> Mono.just((Integer.parseInt(i) * 12)))
                .map(String::valueOf);
    }

    public Flux<String> fluxOnErrorContinue(int from) {
        return countDownInt(0, from, 1000)
                .flatMap(i -> {
                    if (i == 1) return Mono.just(i / 0);
                    return Mono.just(i);
                })
                .log()
                .onErrorResume(ArithmeticException.class,
                        (resp) -> Mono.just(1))
                .onErrorContinue((err, obj) -> log.error("Error occurred on {} detail message {}", obj, err.getMessage()))
                .map(String::valueOf);
    }

    public Flux<String> fluxBuffer(int end) {
        return countDownInt(0, end, 300)
                .bufferTimeout(3, Duration.ofMillis(1000))
                .map(list -> new Gson().toJson(list) + " END")
                .log();
    }

    public Flux<String> fluxOnErrorResume(int end) {
        return countDownInt(0, end, 1000)
                .flatMap(i -> {
                    if (i == 5) return Mono.just(i / 0);
                    return Mono.just(i);
                })
                .onErrorResume(ArithmeticException.class,
                        (resp) -> Flux.fromStream(IntStream.range(0, 5).map(i -> (5 - i)).mapToObj(i -> i).collect(Collectors.toList()).stream()))
                .map(String::valueOf);
    }

    public Flux<String> countDown(int from) {
        List<Integer> intStream = IntStream.range(0, from)
                .map(i -> (from - i)).mapToObj(i -> i).collect(Collectors.toList());

        return Flux
                .fromStream(intStream.stream())
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000))
                .log();
    }

    public Flux<Integer> countDownInt(int end, int duration) {
        return countDownInt(0, end, duration);
    }

    public Flux<Integer> countDownInt(int start, int end, int duration) {
        List<Integer> intStream = IntStream.range(start, end)
                .map(i -> (end - i)).mapToObj(i -> i).collect(Collectors.toList());

        return Flux
                .fromStream(intStream.stream())
                .delayElements(Duration.ofMillis(duration))
                .log();
    }

    public Flux<String> zip(int from) {
        List<Integer> countDown =
                IntStream.range(0, from)
                        .map(i -> (from - i))
                        .mapToObj(i -> i)
                        .collect(Collectors.toList());

        Flux<String> countDownFlux1 =
                Flux.fromStream(countDown.stream())
                        .filter(i -> (i % 2 == 0))
                        .map(String::valueOf)
                        .delayElements(Duration.ofMillis(1000))
                        .log();

        Flux<String> countDownFlux2 =
                Flux
                        .fromStream(countDown.stream())
                        .filter(i -> (i % 2 == 1))
                        .map(String::valueOf)
                        .delayElements(Duration.ofMillis(2400))
                        .log();

        return Flux.zip(countDownFlux1, countDownFlux2)
                .map(tuple -> tuple.getT1() + " - " + tuple.getT2());
    }

    private List<Integer> countdownList(int from) {
        return countdownList(0, from);
    }

    private List<Integer> countdownList(int start, int from) {
        return IntStream.range(start, from)
                .map(i -> (from - i))
                .mapToObj(i -> i)
                .collect(Collectors.toList());
    }

    public Flux<String> concat(int from) {
        Flux<String> countDown1 =
                Flux
                        .fromStream(countdownList(from).stream())
                        .filter(i -> (i % 2 == 0))
                        .map(a -> "stream left =>" + a)
                        .delayElements(Duration.ofMillis(2000))
                        .log();

        Flux<String> countDown2 =
                Flux
                        .fromStream(countdownList(from).stream())
                        .filter(i -> (i % 2 == 0))
                        .map(a -> "stream right =>" + a)
                        .delayElements(Duration.ofMillis(1000))
                        .log();

        return Flux.concat(countDown1, countDown2);
    }

    public Flux<String> merge(int from) {
        Flux<String> countDown1 =
                Flux
                        .fromStream(countdownList(from).stream())
                        .filter(i -> (i % 2 == 0))
                        .map(a -> "stream left =>" + a)
                        .delayElements(Duration.ofMillis(500))
                        .log();

        Flux<String> countDown2 =
                Flux
                        .fromStream(countdownList(from).stream())
                        .filter(i -> (i % 2 == 0))
                        .map(a -> "stream right =>" + a)
                        .delayElements(Duration.ofMillis(900))
                        .log();

        return Flux.merge(countDown1, countDown2);
    }

    public Flux<String> fluxTake(Integer from, Optional<Long> maybeMs, Optional<Long> maybeCount) {
        Flux<Integer> countDownFlux = countDownInt(from, 1000);
        return maybeMs.isPresent() ? countDownFlux.take(Duration.ofMillis(maybeMs.get())).map(String::valueOf)
                : countDownFlux.take(maybeCount.get()).map(String::valueOf);
    }
}
