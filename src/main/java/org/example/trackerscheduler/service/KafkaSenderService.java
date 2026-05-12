package org.example.trackerscheduler.service;

import org.example.trackerscheduler.dto.email.EmailLetterModel;

public interface KafkaSenderService {
    void sendMessageToKafka(EmailLetterModel emailLetterModel);
}
