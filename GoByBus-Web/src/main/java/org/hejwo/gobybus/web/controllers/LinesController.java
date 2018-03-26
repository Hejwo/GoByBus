package org.hejwo.gobybus.web.controllers;

import com.google.gson.Gson;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.hejwo.gobybus.web.dto.Position;
import org.hejwo.gobybus.web.invocationtimeacpect.LogInvocationTime;
import org.hejwo.gobybus.web.services.LocationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.StringJoiner;

@LogInvocationTime
@RefreshScope
@RestController
@RequestMapping("lines")
public class LinesController {

    private final LocationDataService locationDataService;

    // TODO: 04.10.17, phejwowski, log requests context

    @Autowired
    public LinesController(LocationDataService locationDataService) {
        this.locationDataService = locationDataService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/days")
    public Set<String> getDays() {
        return locationDataService.findDays();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/lines-list")
    public Set<String> getLinesListForDay(@PathVariable String day) {
        LocalDate localDate = LocalDate.parse(day);
        return locationDataService.findLinesListForDay(localDate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/{lineId}")
    public List<Position> getAllLinePointsForDay(@PathVariable String day, @PathVariable String lineId) {
        LocalDate localDate = LocalDate.parse(day);
        return locationDataService.findAllLinePointsForDay(localDate, lineId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/{lineId}/brigades")
    public Set<String> getBrigadesForLineAndDay(@PathVariable String day, @PathVariable String lineId) {
        LocalDate localDate = LocalDate.parse(day);
        return locationDataService.findBrigadesForLine(localDate, lineId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/{lineId}/{brigadeId}")
    public List<Position> getPointsForSingleBrigade(@PathVariable String day,
                                                    @PathVariable String lineId,
                                                    @PathVariable String brigadeId) {
        LocalDate localDate = LocalDate.parse(day);
        return locationDataService.findPointsForLine(localDate, lineId, brigadeId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/{lineId}/{brigadeId}/dump")
    public void getCsvPointsForSingleBrigade(@PathVariable String day, @PathVariable String lineId, @PathVariable String brigadeId,
                                             HttpServletResponse response) throws IOException {
        LocalDate localDate = LocalDate.parse(day);

        List<Position> pointsForLine = locationDataService.findPointsForLine(localDate, lineId, brigadeId);
        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + getFilename(localDate, lineId, brigadeId));
        writeCsv(pointsForLine, response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{day}/dump")
    public void getDayDump(@PathVariable String day, HttpServletResponse response) throws IOException {
        LocalDate localDate = LocalDate.parse(day);
        List<Position> allPointsForDay = locationDataService.findAllPointsForDay(localDate);

        response.setContentType("application/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + getDumpName(localDate));
        writeCsv(allPointsForDay, response);
    }

    private String getDumpName(LocalDate localDate) {
        return String.format("%s_dayDump.csv", localDate);
    }

    private String getFilename(LocalDate localDate, String lineId, String brigadeId) {
        return String.format("%s_%s_%s.csv", localDate, lineId, brigadeId);
    }

    private void writeCsv(List<Position> pointsForLine, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        Gson gson = new Gson();
        StringJoiner stringJoiner = new StringJoiner(",\r\n");

        pointsForLine.forEach(position -> stringJoiner.add(gson.toJson(position)));

        writer.append(stringJoiner.toString());
        writer.close();
    }

}
