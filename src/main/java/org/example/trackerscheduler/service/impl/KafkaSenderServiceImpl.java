package org.example.trackerscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trackerscheduler.config.kafka.KafkaProperties;
import org.example.trackerscheduler.dto.email.EmailLetterModel;
import org.example.trackerscheduler.service.KafkaSenderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {
    private final KafkaTemplate<String, EmailLetterModel> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public void sendMessageToKafka(EmailLetterModel emailLetterModel) {
        kafkaTemplate.send(kafkaProperties.getTopic(), emailLetterModel);
        log.info("Sent email to kafka topic: {} - {}", kafkaProperties.getTopic(), emailLetterModel);
    }
}
