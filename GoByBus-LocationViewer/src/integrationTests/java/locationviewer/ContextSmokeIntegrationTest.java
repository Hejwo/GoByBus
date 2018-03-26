package locationviewer;

import locationviewer.configuration.IntegrationTestConfig;
import org.hejwo.gobybus.locationviewer.LocationViewerApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LocationViewerApp.class, EmbeddedMongoAutoConfiguration.class, IntegrationTestConfig.class})
public class ContextSmokeIntegrationTest {

    @Autowired
    private LocationViewerApp app;

    @Test
    public void smokeTest() {

    }
}
