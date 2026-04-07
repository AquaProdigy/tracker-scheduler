package org.example.trackerauth.services;

import lombok.RequiredArgsConstructor;
import org.example.trackerauth.dto.RegisteredUserKafkaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSenderService {
    private final KafkaTemplate<String, RegisteredUserKafkaDto> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    public void sendMessageToKafka(RegisteredUserKafkaDto registeredUserKafkaDto) {
        kafkaTemplate.send(kafkaTopic, registeredUserKafkaDto);
    }
}
