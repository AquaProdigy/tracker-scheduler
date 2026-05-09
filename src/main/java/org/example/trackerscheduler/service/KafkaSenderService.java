package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.trackerscheduler.config.kafka.KafkaProperties;
import org.example.trackerscheduler.model.EmailLetterModel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSenderService {
    private final KafkaTemplate<String, EmailLetterModel> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendMessageToKafka(EmailLetterModel emailLetterModel) {
        kafkaTemplate.send(kafkaProperties.getTopic(), emailLetterModel);
    }
}
