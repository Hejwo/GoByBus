package org.hejwo.gobybus.busstopcrawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusStopApp.class)
public class BusStopAppSmokeIntegrationTest {

    @Autowired
    private BusStopApp busStopApp;

    @Test
    public void smokeTest() {

    }
}
