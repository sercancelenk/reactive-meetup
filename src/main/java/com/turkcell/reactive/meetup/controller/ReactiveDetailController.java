package com.turkcell.reactive.meetup.controller;

import com.turkcell.reactive.meetup.service.ReactiveWorkshopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * @author Created by Sercan CELENK at 2018-12-06
 */

@RestController
@RequestMapping(value = "reactive-detail")
@Slf4j
public class ReactiveDetailController {
    private ReactiveWorkshopService reactiveWorkshopService;

    @Autowired
    public ReactiveDetailController(ReactiveWorkshopService reactiveWorkshopService) {
        this.reactiveWorkshopService = reactiveWorkshopService;
    }

    @GetMapping(value = "countdown/{from}/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFlux(@PathVariable Integer from) {
        return reactiveWorkshopService.countDown(from);
    }

    @GetMapping(value = "countdown/{from}/flux-zip", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxZip(@PathVariable Integer from) {
        return reactiveWorkshopService.zip(from);
    }

    @GetMapping(value = "countdown/{from}/flux-flatmap", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxFlatmap(@PathVariable Integer from) {
        return reactiveWorkshopService.flatMap(from);
    }

    @GetMapping(value = "countdown/{from}/flux-concat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxConcat(@PathVariable Integer from) {
        return reactiveWorkshopService.concat(from);
    }

    @GetMapping(value = "countdown/{from}/flux-merge", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxMerge(@PathVariable Integer from) {
        return reactiveWorkshopService.merge(from);
    }

    @GetMapping(value = "countdown/{from}/flux-onerror-continue", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxOnErrorContinue(@PathVariable Integer from) {
        return reactiveWorkshopService.fluxOnErrorContinue(from);
    }

    @GetMapping(value = "countdown/{from}/flux-onerror-resume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxOnErrorResume(@PathVariable Integer from) {
        return reactiveWorkshopService.fluxOnErrorResume(from);
    }

    @GetMapping(value = "countdown/{from}/flux-buffer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxBuffer(@PathVariable Integer from) {
        return reactiveWorkshopService.fluxBuffer(from);
    }

    @GetMapping(value = "countdown/{from}/take-ms/{ms}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxTakeWithMstus(@PathVariable Integer from,
                                                   @PathVariable Long ms) {
        return reactiveWorkshopService.fluxTake(from, Optional.of(ms), Optional.empty());
    }

    @GetMapping(value = "countdown/{from}/take-count/{count}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> countDownFluxTakeWithCount(@PathVariable Integer from,
                                                   @PathVariable Long count) {
        return reactiveWorkshopService.fluxTake(from, Optional.empty(), Optional.of(count));
    }

}
