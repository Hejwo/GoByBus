package locationviewer.configuration;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hejwo.gobybus.locationviewer.configuration.KafkaConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class IntegrationTestConfig {

    @Bean
    public KafkaConsumer<String, String> consumer(KafkaConsumerConfig consumerConfig) {
        return mock(KafkaConsumer.class);
    }

}
