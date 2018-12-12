package com.turkcell.reactive.meetup.service;

import com.turkcell.reactive.meetup.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaVideoProducer {
    private KafkaSender<String,Comment> commentKafkaSender;


    @PostConstruct
    public void init(){
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        SenderOptions<String, Comment> senderOptions = SenderOptions.create(producerProps);
        commentKafkaSender = KafkaSender.create(senderOptions);
    }

    public Mono<Comment> send(Comment comment){
        SenderRecord<String,Comment,String> senderRecord = SenderRecord.create(new ProducerRecord<>("comments", comment.getId(), comment),comment.getId());
        return commentKafkaSender.send(Mono.just(senderRecord)).next().map(any -> comment);
    }
}
