package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trackerscheduler.config.kafka.KafkaProperties;
import org.example.trackerscheduler.dto.email.EmailLetterModel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSenderService {
    private final KafkaTemplate<String, EmailLetterModel> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendMessageToKafka(EmailLetterModel emailLetterModel) {
        kafkaTemplate.send(kafkaProperties.getTopic(), emailLetterModel);
        log.info("Sent email to kafka topic: {} - {}", kafkaProperties.getTopic(), emailLetterModel);
    }
}
