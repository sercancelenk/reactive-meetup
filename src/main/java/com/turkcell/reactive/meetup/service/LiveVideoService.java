package com.turkcell.reactive.meetup.service;

import com.turkcell.reactive.meetup.model.Comment;
import com.turkcell.reactive.meetup.model.Reactions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveVideoService {

    private WebClient webClient;
    private final KafkaVideoProducer kafkaVideoProducer;
    private final CommentService commentService;
    private final RedisService redisService;

    @Value("${facebook.graph.api.accessToken:Please provide baseUrl}")
    private String accessToken;

    @Value("${facebook.graph.api.baseUrl:Please provide id}")
    private String GRAPH_API_BASE_URL;

    @PostConstruct
    public void init() {
        ClientHttpConnector connector = new ReactorClientHttpConnector();
        webClient = WebClient.builder().clientConnector(connector).baseUrl(GRAPH_API_BASE_URL).build();
    }

    private <T> Flux<T> getFromFacebookGrapAPI(String url, Class<T> clazz) {
        return webClient
                .get()
                .uri(url)
                .headers(httpHeaders -> {
                })
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> {
                            log.error("Http Status : " + clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class).flatMap(text -> Mono.error(new RuntimeException(text)));
                        }
                )
                .bodyToFlux(clazz)
                .onErrorMap(t -> {
                    log.error("Exception when calling facebook graph api", t);
                    return t;
                });
    }

    public Flux<Comment> getCommentsSaved(long liveVideoId) {
        Flux<Comment> facebookFlux = getCommentsFromFacebook(liveVideoId);

        return facebookFlux.flatMap(comment -> {
            Mono<Comment> kafkaMono = kafkaVideoProducer.send(comment).map(a -> comment);
            Mono<Comment> h2Mono = commentService.saveComment(comment);
            Mono<Comment> redisMono = redisService.save(liveVideoId,comment);
            return Mono.zip(kafkaMono, h2Mono,redisMono).map(a -> a.getT2());
        });
    }

    public Flux<Comment> getCommentsFromFacebook(long liveVideoId) {
        String url = String.format("%s/%s/live_comments?access_token=%s&fields=from,message", GRAPH_API_BASE_URL, liveVideoId, accessToken);
        log.info("Get commands url : {}", url);
        return getFromFacebookGrapAPI(url, Comment.class)
                .onErrorContinue((throwable, comment) -> {
                    log.error("Exception when getting data from facebook, comment : {}", comment, throwable);
                });
    }

    public Flux<Reactions> getReactions(long liveVideoId) {
        String url = String.format("%s/%s/live_reactions?access_token=%s&fields=reaction_stream", GRAPH_API_BASE_URL, liveVideoId, accessToken);
        return getFromFacebookGrapAPI(url, Reactions.class);
    }
}
