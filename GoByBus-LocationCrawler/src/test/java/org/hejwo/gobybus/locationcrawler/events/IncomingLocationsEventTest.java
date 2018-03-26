package org.hejwo.gobybus.locationcrawler.events;

import org.assertj.core.util.Lists;
import org.hejwo.gobybus.commons.domain.LocationData;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingLocationsEventTest {

    @Test
    public void create_eventProperlyCreated_whenEmptyLocations() throws Exception {
        IncomingLocationsEvent event = IncomingLocationsEvent.create(Lists.newArrayList());

        assertThat(event.getSource()).isEmpty();
    }

    @Test
    public void create_eventProperlyCreated_whenManyLocations() throws Exception {
        LocationData locationData1 = LocationData.builder().build();
        LocationData locationData2 = LocationData.builder().build();


        ArrayList<LocationData> locations = Lists.newArrayList(locationData1, locationData2);
        IncomingLocationsEvent event = IncomingLocationsEvent.create(locations);

        assertThat(event.getSource()).hasSize(2);
        assertThat(event.getSource()).isEqualTo(locations);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_eventFailed_whenNull() throws Exception {
        IncomingLocationsEvent.create(null);
    }

    @Test
    public void eventShouldHaveStringImplementation() {
        LocationData location1 = LocationData.builder().status("RUNNING").firstLine("21").build();
        IncomingLocationsEvent event = IncomingLocationsEvent.create(Lists.newArrayList(location1));
        String string = event.toString();

        assertThat(string).startsWith("IncomingLocationsEvent{source=");
    }
}