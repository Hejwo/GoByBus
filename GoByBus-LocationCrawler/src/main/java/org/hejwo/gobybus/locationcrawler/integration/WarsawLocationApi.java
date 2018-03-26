package org.hejwo.gobybus.locationcrawler.integration;

import feign.Param;
import feign.RequestLine;
import org.hejwo.gobybus.commons.domain.LocationData;

import java.util.List;

public interface WarsawLocationApi {

    @RequestLine("GET /api/action/wsstore_get/?id=c7238cfe-8b1f-4c38-bb4a-de386db7e776&apikey={apiKey}")
    List<LocationData> getAllLines(@Param("apiKey") String apiKey);

}