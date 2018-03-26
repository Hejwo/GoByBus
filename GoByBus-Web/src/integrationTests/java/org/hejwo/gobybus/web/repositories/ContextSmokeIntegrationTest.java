package org.hejwo.gobybus.web.repositories;

import org.hejwo.gobybus.web.WebApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebApp.class)
public class ContextSmokeIntegrationTest {

    @Autowired
    private WebApp webApp;

    @Test
    public void smokeTest() {
    }

}
