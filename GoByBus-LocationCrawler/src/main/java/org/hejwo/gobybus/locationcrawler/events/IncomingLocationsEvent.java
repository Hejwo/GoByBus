package org.hejwo.gobybus.locationcrawler.events;

import org.hejwo.gobybus.commons.domain.LocationData;
import org.springframework.context.ApplicationEvent;

import java.util.List;

import static org.springframework.util.Assert.notNull;

public class IncomingLocationsEvent extends ApplicationEvent {

    private IncomingLocationsEvent(List<LocationData> source) {
        super(source);
        notNull(source);
    }

    public static IncomingLocationsEvent create(List<LocationData> locations) {
        return new IncomingLocationsEvent(locations);
    }

    @Override
    public List<LocationData> getSource() {
        return (List<LocationData>)super.getSource();
    }

    @Override
    public String toString() {
        return "IncomingLocationsEvent{" +
                "source=" + source +
                '}';
    }
}
