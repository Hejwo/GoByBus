package locationviewer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hejwo.gobybus.locationviewer.LocationViewerApp;
import org.hejwo.gobybus.locationviewer.configuration.KafkaConsumerConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LocationViewerApp.class, EmbeddedMongoAutoConfiguration.class, KafkaConnectionTimeoutIntegrationTest.class})
@Configuration
public class KafkaConnectionTimeoutIntegrationTest {

    @Autowired
    private LocationViewerApp app;

    @Test
    @Ignore
    public void kafkaConnectionTimeoutTest() {
        // TODO: 07.10.17, phejwowski,
    }

    @Bean
    public KafkaConsumer<String, String> consumer(KafkaConsumerConfig consumerConfig) {
        return new KafkaConsumer<>(consumerConfig.asProperties());
    }


}
