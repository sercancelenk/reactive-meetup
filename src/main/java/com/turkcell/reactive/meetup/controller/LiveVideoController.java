package com.turkcell.reactive.meetup.controller;

import com.turkcell.reactive.meetup.model.Comment;
import com.turkcell.reactive.meetup.model.Reactions;
import com.turkcell.reactive.meetup.service.KafkaVideoConsumer;
import com.turkcell.reactive.meetup.service.LiveVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LiveVideoController {
    private final LiveVideoService liveVideoService;
    private final KafkaVideoConsumer kafkaVideoConsumer;

    @GetMapping(path = "video/{videoId}/comments", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Comment> comments(@PathVariable long videoId) {
        return liveVideoService
                .getCommentsFromFacebook(videoId)
                .doOnError(t -> log.error("Ex", t));
    }

    @GetMapping(path = "video/{videoId}/comments/h2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Comment> commentsFromH2(@PathVariable long videoId) {
        return liveVideoService
                .getCommentsSaved(videoId)
                .doOnError(t -> log.error("Ex", t));
    }

    @GetMapping(path = "video/{videoId}/reactions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Reactions> reactions(@PathVariable long videoId) {
        return liveVideoService
                .getReactions(videoId)
                .doOnError(t -> log.error("Ex", t));
    }

    @GetMapping(path = "video/{videoId}/comments/kafka", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Comment> commentsFromKafka(@PathVariable String videoId) {
        return kafkaVideoConsumer.getCommentStream();
    }

    /*
    @GetMapping(path = "video/{videoId}/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tuple2<String,String>> all(@PathVariable long videoId) {
        return liveVideoService
                .getBothZipped(videoId)
                .doOnError(t -> log.error("Ex",t));
    }*/


}
