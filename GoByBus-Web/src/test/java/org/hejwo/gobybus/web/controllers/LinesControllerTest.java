package org.hejwo.gobybus.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.jackson.datatype.VavrModule;
import org.hejwo.gobybus.web.config.BasicConfiguration;
import org.hejwo.gobybus.web.dto.Position;
import org.hejwo.gobybus.web.services.LocationDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.time.LocalDate;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LinesControllerTest {

    private MockMvc mockMvc;
    private LinesController linesController;

    @Mock
    private LocationDataService locationDataService;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        MappingJackson2HttpMessageConverter converter = getMessageConverter();

        linesController = new LinesController(locationDataService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(linesController)
            .setViewResolvers((ViewResolver) (viewName, locale) -> new MappingJackson2JsonView())
            .setMessageConverters(converter)
            .build();

        mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
    }

    private MappingJackson2HttpMessageConverter getMessageConverter() {
        BasicConfiguration basicConfiguration = new BasicConfiguration();
        return basicConfiguration.jackson2HttpMessageConverter(basicConfiguration.builderCustomizer());
    }

    @Test
    public void getLinesListForDay_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findLinesListForDay(eq(selectedDate)))
            .thenReturn(HashSet.of("1", "2", "3", "4"));

        mockMvc.perform(
            get("/lines/2017-10-28/lines-list").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[\"1\",\"2\",\"3\",\"4\"]"));
    }

    @Test
    public void getLinesListForDay_shouldReturnEmpty() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findLinesListForDay(eq(selectedDate)))
            .thenReturn(HashSet.of());

        mockMvc.perform(
            get("/lines/2017-10-28/lines-list").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    public void getAllLinePointsForDay_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findAllLinePointsForDay(eq(selectedDate), eq("9")))
            .thenReturn(List.of(
                createPosition("9", "11", "12:22:11"),
                createPosition("9", "211", "12:22:21")
            ));

        String expectedJson = "[{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:11\"},{\"line\":\"9\",\"brigade\":\"211\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:21\"}]";

        mockMvc.perform(
            get("/lines/2017-10-28/9").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    public void getAllLinePointsForDay_shouldReturnEmpty() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findAllLinePointsForDay(eq(selectedDate), eq("9")))
            .thenReturn(List.of());

        mockMvc.perform(
            get("/lines/2017-10-28/9").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[ ]"));
    }

    @Test
    public void getBrigadesForLineAndDay_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findBrigadesForLine(eq(selectedDate), eq("9")))
            .thenReturn(HashSet.of("21c", "17a"));

        mockMvc.perform(
            get("/lines/2017-10-28/9/brigades").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[\"21c\", \"17a\"]"));
    }

    @Test
    public void getBrigadesForLineAndDay_returnEmpty() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findBrigadesForLine(eq(selectedDate), eq("9")))
            .thenReturn(HashSet.of());

        mockMvc.perform(
            get("/lines/2017-10-28/9/brigades").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    public void getPointsForSingleBrigade_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findPointsForLine(eq(selectedDate), eq("9"), eq("45a")))
            .thenReturn(List.of(
                createPosition("9", "11", "12:22:11"),
                createPosition("9", "11", "12:22:21")
            ));

        String expectedJson = "[{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:11\"},{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:21\"}]";

        mockMvc.perform(
            get("/lines/2017-10-28/9/45a").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    public void getPointsForSingleBrigade_returnEmpty() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findPointsForLine(eq(selectedDate), eq("9"), eq("45a")))
            .thenReturn(List.of());

        mockMvc.perform(
            get("/lines/2017-10-28/9/45a").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }

    @Test
    public void getCsvPointsForSingleBrigade_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findPointsForLine(eq(selectedDate), eq("9"), eq("45a")))
            .thenReturn(List.of(
                createPosition("9", "11", "12:22:11"),
                createPosition("9", "11", "12:22:21")
            ));

        String expectedContent = "{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:11\"},\r\n" +
            "{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:21\"}";

        mockMvc.perform(
            get("/lines/2017-10-28/9/45a/dump").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=2017-10-28_9_45a.csv"))
            .andExpect(content().contentType("application/csv"))
            .andExpect(content().string(expectedContent));
    }

    @Test
    public void getDayDump_shouldWork() throws Exception {
        LocalDate selectedDate = LocalDate.of(2017, 10, 28);

        when(locationDataService.findAllPointsForDay(eq(selectedDate)))
            .thenReturn(List.of(
                createPosition("9", "11", "12:22:11"),
                createPosition("9", "11", "12:22:21")
            ));

        String expectedContent = "{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:11\"},\r\n" +
            "{\"line\":\"9\",\"brigade\":\"11\",\"latitude\":20.2,\"longitude\":33.3333331,\"time\":\"12:22:21\"}";

        mockMvc.perform(
            get("/lines/2017-10-28/dump").accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=2017-10-28_dayDump.csv"))
            .andExpect(content().contentType("application/csv"))
            .andExpect(content().string(expectedContent));
    }

    private Position createPosition(String line, String brigade, String time) {
        return Position.builder()
            .line(line).brigade(brigade)
            .time(time)
            .latitude(20.2).longitude(33.3333331)
            .build();
    }


}