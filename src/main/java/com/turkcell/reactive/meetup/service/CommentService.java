package com.turkcell.reactive.meetup.service;

import com.turkcell.reactive.meetup.model.Comment;
import com.turkcell.reactive.meetup.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Mono<Comment> saveComment(Comment comment) {
        return Mono.defer(() -> Mono.just(commentRepository.save(comment)))
                .subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(10)));
    }

}
