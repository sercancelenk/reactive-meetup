package com.turkcell.reactive.meetup.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestMapping("/reactive")
@RestController
@Slf4j
public class ReactiveController {

    private Flux<String> hotStream = Flux.fromStream(integers(100).stream())
            .map(String::valueOf)
            .delayElements(Duration.ofMillis(1000))
            .publish().autoConnect();


    public List<Integer> integers(int from){
        return IntStream.range(0,from)
                .map(i -> (from - i))
                .mapToObj(a -> a).collect(Collectors.toList());
    }

    @GetMapping(value = "/countdown/{from}/flux",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> flux(@PathVariable int from){
        return Flux.fromStream(integers(from).stream())
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000));
    }

    @GetMapping(value = "/countdown/fluxhot",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxHot(){
        return hotStream;
    }

    @GetMapping(value = "/countdown/{from}/fluxzip",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxZip(@PathVariable int from){
        Flux<String> flux1 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 0))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000));

        Flux<String> flux2 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 1))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(3000));

        return flux1.zipWith(flux2).map(tuple -> tuple.getT1() + "-" + tuple.getT2());
    }

    @GetMapping(value = "/countdown/{from}/fluxconcat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxConcat(@PathVariable int from){
        Flux<String> flux1 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 0))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000));

        Flux<String> flux2 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 1))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000));

        return flux1.concatWith(flux2);
    }

    @GetMapping(value = "/countdown/{from}/fluxmerge",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxMerge(@PathVariable int from){
        Flux<String> flux1 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 0))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(1000));

        Flux<String> flux2 = Flux.fromStream(integers(from).stream())
                .filter(a -> (a % 2 == 1))
                .map(String::valueOf)
                .delayElements(Duration.ofMillis(2500));

        return flux1.mergeWith(flux2);
    }

    @GetMapping(value = "/countdown/{from}/fluxflatmap",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxFlatmap(@PathVariable int from){
        Flux<Integer> flux1 = Flux.fromStream(integers(from).stream())
                .delayElements(Duration.ofMillis(1000));

        return flux1.flatMap(a -> Flux.fromStream(Arrays.asList(a+10,a+11,a+12).stream()))
                .map(String::valueOf);

    }





}
