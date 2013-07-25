package com.synaway.oneplaces.test;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.service.SpotService;

@ContextConfiguration(locations = { "classpath:application-context.xml",
        "file:src/main/webapp/WEB-INF/1places-servlet.xml", "file:src/test/resources/application-context-test.xml" })
abstract public class AbstractIntegrationTest {
    @Autowired
    protected SpotService spotService;

    protected Spot addSpot(User user) {
        return addSpot(user, 19.885, 19.991, 50.0208, 50.0831);
    }

    protected Spot addSpot(User user, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        Spot spot = new Spot();
        spot.setTimestamp(new Date());
        spot.setUser(user);
        spot.setStatus("free");
        spot.setFlag("fake");

        Random r = new Random();
        double latitude = minLatitude + (maxLatitude - minLatitude) * r.nextDouble();
        double longitude = minLongitude + (maxLongitude - minLongitude) * r.nextDouble();

        spot.setLocation(spotService.createPoint(latitude, longitude));

        spot = spotService.saveSpot(spot);
        return spot;
    }
}
