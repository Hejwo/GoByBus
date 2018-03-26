package org.hejwo.gobybus.web;

import com.google.common.collect.Lists;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.web.repositories.LocationDataRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@SpringBootTest(classes = {
    WebApp.class,
    EmbeddedMongoAutoConfiguration.class
})
public class WebE2EIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private LocationDataRepository repository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        repository.deleteAll();
        createTestData();
    }

    @Test
    public void getDays_shouldReturnDays() throws Exception {
        mockMvc.perform(get("/lines/days")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]", is("2017-10-12")))
            .andExpect(jsonPath("$[1]", is("2017-10-31")))
            .andExpect(jsonPath("$[2]", is("2017-11-01")));
    }

    @Test
    public void shouldReturn_linesListForDay() throws Exception {
        String response = mockMvc.perform(get("/lines/2017-10-31/lines-list")
            .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        assertThatJson(response).isEqualTo("[\"9\",\"10\"]");
    }

    @Test
    public void shouldReturn_singleLinePositionsForDay() throws Exception {
        mockMvc.perform(get("/lines/2017-10-31/9")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[*]", hasSize(3)))
            .andExpect(jsonPath("$[*]['line']", hasSize(3)))
            .andExpect(jsonPath("$[*]['brigade']", hasSize(3)));
    }

    @Test
    public void shouldReturn_brigadesListForDay() throws Exception {
        String response = mockMvc.perform(get("/lines/2017-10-31/9/brigades")
            .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();

        assertThatJson(response).isEqualTo("[\"27\",\"17\"]");
    }

    @Test
    public void shouldReturn_singleLinePositionsForBrigadeAndDay() throws Exception {
        mockMvc.perform(get("/lines/2017-10-31/9/27")
            .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[*]['line']", hasSize(2)))
                .andExpect(jsonPath("$[*]['brigade']", hasSize(2)));
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    private LocationData createLocationData(String line, String brigade, LocalDateTime dateTime,
                                            Double longitude, Double latitude) {
        return LocationData.builder()
            .status("RUNNING")
            .brigade(brigade)
            .firstLine(line)
            .lines(Collections.singletonList(line))
            .lowFloor(true)
            .time(dateTime)
            .longitude(longitude)
            .latitude(latitude)
            .build();
    }

    private void createTestData() {
        LocationData location1 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 0), 12d, 12d);
        LocationData location2 = createLocationData("10", "27", LocalDateTime.of(2017, 10, 31, 12, 1), 12d, 12d);
        LocationData location3 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 31, 12, 2), 12d, 12d);
        LocationData location4 = createLocationData("10", "25", LocalDateTime.of(2017, 10, 31, 12, 3), 12d, 12d);
        LocationData location5 = createLocationData("9", "17", LocalDateTime.of(2017, 10, 31, 12, 4), 12d, 12d);

        LocationData location6 = createLocationData("9", "15", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location7 = createLocationData("9", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location8 = createLocationData("11", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);
        LocationData location9 = createLocationData("57", "27", LocalDateTime.of(2017, 10, 12, 12, 3), 12d, 12d);

        LocationData location10 = createLocationData("3C", "11", LocalDateTime.of(2017, 11, 1, 12, 3), 12d, 12d);

        List<LocationData> locations = Lists.newArrayList(
            location1, location2, location3, location4, location5,
            location6, location7, location8, location9, location10);
        repository.save(locations);
    }
}
