package org.example.trackerscheduler.service;

import lombok.RequiredArgsConstructor;
import org.example.trackerscheduler.model.EmailLetterModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSenderService {
    private final KafkaTemplate<String, EmailLetterModel> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    public void sendMessageToKafka(EmailLetterModel emailLetterModel) {
        kafkaTemplate.send(kafkaTopic, emailLetterModel);
    }
}
