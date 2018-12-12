package com.turkcell.reactive.meetup.service;

import com.turkcell.reactive.meetup.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaVideoConsumer {

    private Flux<Comment> commentFlux;

    @PostConstruct
    public void init() {
        ReceiverOptions<String, Comment> receiverOptions = getReceiverOptions("localhost:9092",
                "comments", "comments.group");

        commentFlux = KafkaReceiver.create(receiverOptions).receiveAutoAck().concatMap(r -> r)
                .map(receiverRecord -> receiverRecord.value())
                .onErrorContinue((exception, user) ->
                        log.error("Exception when reading user login data from kafka : {}", user, exception))
                .publish().autoConnect();

        commentFlux.subscribe().dispose();
    }

    private ReceiverOptions<String, Comment> getReceiverOptions(String bootstrapServers,
                                                                String topic, String groupId) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.turkcell.reactive.meetup.model");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return ReceiverOptions.<String, Comment>create(consumerProps)
                .subscription(Collections.singleton(topic));
    }

    public Flux<Comment> getCommentStream() {
        return this.commentFlux;
    }

}
