package org.hejwo.gobybus.locationcrawler;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hejwo.gobybus.locationcrawler.config.KafkaLocationProducerConfig;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Future;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class IntegrationTestKafkaConfig {

    @Bean
    public ArgumentCaptor<ProducerRecord> kafkaProducerArgumentCaptor() {
        return ArgumentCaptor.forClass(ProducerRecord.class);
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer(KafkaLocationProducerConfig kafkaLocationProducerConfig,
                                                       ArgumentCaptor<ProducerRecord> kafkaProducerArgumentCaptor) {
        KafkaProducer producer = mock(KafkaProducer.class);
        when(producer.send(kafkaProducerArgumentCaptor.capture(), any(Callback.class)))
            .thenReturn(mock(Future.class));

        return producer;
    }

}
