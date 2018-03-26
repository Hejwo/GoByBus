package org.hejwo.gobybus.busstopcrawler.integration;

import feign.Param;
import feign.RequestLine;
import org.hejwo.gobybus.busstopcrawler.integration.dto.BusStopDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.LineDTO;
import org.hejwo.gobybus.busstopcrawler.integration.dto.TimetableDTO;

import java.util.List;

public interface WarsawBusStopApi {

    @RequestLine("GET /api/action/dbstore_get/?id=ab75c33d-3a26-4342-b36a-6e5fef0a3ac3&apikey={apiKey}")
    List<BusStopDTO> getAllBusStops(@Param("apiKey") String apiKey);

    @RequestLine("GET /api/action/dbtimetable_get?id=88cd555f-6f31-43ca-9de4-66c479ad5942&busstopId={busStopId}&busstopNr={busStopNr}&apikey={apiKey}")
    List<LineDTO> getLinesForBusStop(@Param("busStopId") String busStopId, @Param("busStopNr") String busStopNr, @Param("apiKey") String apiKey);

    @RequestLine("GET /api/action/dbtimetable_get?id=e923fa0e-d96c-43f9-ae6e-60518c9f3238&busstopId={busStopId}&busstopNr={busStopNr}&line={line}&apikey={apiKey}")
    List<TimetableDTO> getTimetablesForLineAndBusStop(@Param("busStopId") String busStopId, @Param("busStopNr") String busStopNr, @Param("line") String line, @Param("apiKey") String apiKey);

}