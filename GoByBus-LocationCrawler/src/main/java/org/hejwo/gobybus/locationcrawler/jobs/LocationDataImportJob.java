package org.hejwo.gobybus.locationcrawler.jobs;

import lombok.extern.slf4j.Slf4j;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.hejwo.gobybus.locationcrawler.events.IncomingLocationsEvent;
import org.hejwo.gobybus.locationcrawler.integration.WarsawLocationApi;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiNotRetryableException;
import org.hejwo.gobybus.locationcrawler.integration.exceptions.WarsawApiRetryableException;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
public class LocationDataImportJob implements Runnable {

    private final WarsawLocationApi warsawLocationApi;
    private final String warsawApiKey;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public LocationDataImportJob(@Value("${openData.warsawApi.key}") String warsawApiKey,
                                 WarsawLocationApi warsawLocationApi,
                                 ApplicationEventPublisher eventPublisher) {
        this.warsawLocationApi = warsawLocationApi;
        this.warsawApiKey = warsawApiKey;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run() {
        try {
            List<LocationData> locationDataList = warsawLocationApi.getAllLines(warsawApiKey);
            eventPublisher.publishEvent(IncomingLocationsEvent.create(locationDataList));
        } catch (WarsawApiNotRetryableException | WarsawApiRetryableException exception) {
            log.error("Exception has occurred during location's import. Ex: ", exception);
            // TODO: 06.10.17, phejwowski, Error counting strategy ?
        }
    }

}