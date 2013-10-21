package com.synaway.oneplaces.test;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.synaway.oneplaces.controller.rest.SpotController;
import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SpotControllerIntegrationTest extends AbstractIntegrationTest {

    private static Logger logger = Logger.getLogger(SpotControllerIntegrationTest.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Autowired
    MockHttpServletRequest request;

    @Autowired
    SpotController spotController;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    UserLocationRepository userLocationRepository;

    @Before
    public void cleanDatabase() throws Exception {
        accessTokenRepository.deleteAllInBatch();
        spotRepository.deleteAllInBatch();
        userLocationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

    }

    @Test
    public void getAllSpotsShouldReturnProperDate() throws MissingServletRequestParameterException {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user);
        }

        List<Spot> spots = (List<Spot>) spotController.getAllSpots(0.0, 0.0, 100, null ,null).get("spots");
        Assert.assertEquals(0, spots.size());

        spots = (List<Spot>) spotController.getAllSpots(50.05, 19.7, 30000, null, null).get("spots");
        Assert.assertEquals(10, spots.size());
    }

    @Test
    public void testBugWithSpotsAndStatistics() throws MissingServletRequestParameterException {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user, 2.350, 2.351, 48.8550, 48.8559);
        }
        for (int i = 0; i < 10; i++) {
            addSpot(user, 22.350, 22.351, 48.8550, 48.8559);
        }

        Map<String, Object> response = (Map<String, Object>) spotController.getAllSpots(0.0, 0.0, 100, null, null);
        Assert.assertEquals(0, ((List<Spot>) response.get("spots")).size());
        Assert.assertEquals(Long.valueOf(10), (Long) response.get("ttl3"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl6"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl9"));

        response = (Map<String, Object>) spotController.getAllSpots(50.05, 19.7, 30000, null, null);
        Assert.assertEquals(0, ((List<Spot>) response.get("spots")).size());
        Assert.assertEquals(Long.valueOf(10), (Long) response.get("ttl3"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl6"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl9"));

        response = (Map<String, Object>) spotController.getAllSpots(48.8559, 2.351, 10000, null, null);
        Assert.assertEquals(10, ((List<Spot>) response.get("spots")).size());
        Assert.assertEquals(Long.valueOf(10), (Long) response.get("ttl3"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl6"));
        Assert.assertEquals(Long.valueOf(0), (Long) response.get("ttl9"));
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionLatitude()
            throws MissingServletRequestParameterException {
        spotController.getAllSpots(null, 19.7, 20000, null, null);
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionLongitude()
            throws MissingServletRequestParameterException {
        spotController.getAllSpots(50.05, null, 20000, null, null);
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionRadius()
            throws MissingServletRequestParameterException {
        spotController.getAllSpots(50.05, 19.7, null, null, null);
    }

    @Transactional
    @Test
    public void addSpotShouldReturnProperData() throws Exception {
        User user = createUser("john", "password");

        Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + ", \"status\" : \"somestatus\" }");

        Assert.assertEquals(19.856278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

        spot = spotRepository.findOne(spot.getId());

        Assert.assertEquals(19.856278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

    }

    @Transactional
    @Test
    public void updateSpotShouldReturnProperData() throws Exception {
        User user = createUser("john", "password");

        Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + ", \"status\" : \"somestatus\" }");
        spot = spotController.updateSpot("{\"longitude\": 19.756278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + ", \"spotId\" : " + spot.getId() + ", \"status\" : \"somestatus\" }");

        Assert.assertEquals(19.756278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

    }

    @Transactional
    @Test(expected = MissingServletRequestParameterException.class)
    public void updateSpotShouldThrow() throws Exception {
        User user = createUser("john", "password");

        Spot spot = spotController.updateSpot("{\"longitude\": 19.756278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + ", \"status\" : \"somestatus\" }");

        Assert.assertEquals(19.756278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

    }

    @Transactional
    @Test
    public void getSpotTest() throws MissingServletRequestParameterException, UserException, IOException {
        User user = createUser("john", "password");

        Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + ", \"status\" : \"somestatus\" }");
        spot = spotController.getSpot(spot.getId());

        Assert.assertEquals(19.856278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

        spot = spotRepository.findOne(spot.getId());

        Assert.assertEquals(19.856278, spot.getLocation().getX(), 0);
        Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
        Assert.assertEquals(user.getId(), spot.getUser().getId());

    }

    @Transactional
    @Test
    public void shouldGetSpotsListTest() throws Exception {
        User user = createUser("john", "password");
        AccessToken accessToken = userService.getToken("john", "password");

        request.addParameter("access_token", accessToken.getToken());

        for (int i = 0; i < 10; i++) {
            spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : " + user.getId()
                    + ", \"status\" : \"somestatus\" }");
        }

        for (int i = 0; i < 10; i++) {
            spotController.addSpot("{\"longitude\": 18.856278, \"latitude\": 58.06063, \"userId\" : " + user.getId()
                    + ", \"status\" : \"somestatus\" }");
        }

        Assert.assertEquals(10, ((List) spotController.getAllSpots(50.06063, 19.856278, 100, null, null).get("spots")).size());
        Assert.assertEquals(10, ((List) spotController.getAllSpots(58.06063, 18.856278, 100, null, null).get("spots")).size());
    }

    @Transactional
    @Test
    public void userTrackingTest() throws Exception {
        User user = createUser("john", "password");
        AccessToken accessToken = userService.getToken("john", "password");

        request.addParameter("access_token", accessToken.getToken());

        for (int i = 0; i < 10; i++) {
            spotController.getAllSpots(50.06063, 19.856278, 100, 50.06063, 19.856278);
        }

        Assert.assertEquals(10, userLocationRepository.count());
        List<UserLocation> list = userLocationRepository.findByUserOrderByTimestampDesc(user, new PageRequest(0, 10));
        Iterator<UserLocation> it = list.iterator();

        Date previous = new Date();
        while (it.hasNext()) {
            UserLocation userLocation = (UserLocation) it.next();
            Assert.assertTrue("wrong order of user locations", previous.getTime() >= userLocation.getTimestamp()
                    .getTime());
            Assert.assertEquals(19.856278, userLocation.getLocation().getX(), 0);
            Assert.assertEquals(50.06063, userLocation.getLocation().getY(), 0);

            previous = userLocation.getTimestamp();
        }
    }

    @Transactional
    @Test
    public void setOccupySpotShouldReturnProperData() {
        User user = createUser("john", "password");
        Spot spot = addSpot(user);

        Assert.assertEquals("free", spotService.getSpot(spot.getId()).getStatus());

        spotController.setOccupy(spot.getId());

        Assert.assertEquals("occupied", spotService.getSpot(spot.getId()).getStatus());

    }

    @Transactional
    @Test
    public void deleteSpotShouldReturnProperData() throws Exception {
        User user = createUser("john", "password");
        Spot spot = addSpot(user);
        spotController.deleteSpot(spot.getId());
        Assert.assertNull(spotService.getSpot(spot.getId()));
    }

    @Transactional
    @Test
    public void randomSpotShouldReturnProperData() throws Exception {
        double minLatitude = 19.885;
        double maxLatitude = 19.991;
        double minLongitude = 50.0208;
        double maxLongitude = 50.0831;
        User user = createUser("john", "password");
        Spot spot = spotController.addSpot(minLatitude, minLongitude, maxLatitude, maxLongitude);

        Assert.assertTrue(spot.getLocation().getY() >= minLongitude);
        Assert.assertTrue(spot.getLocation().getY() <= maxLongitude);
        Assert.assertTrue(spot.getLocation().getX() >= minLatitude);
        Assert.assertTrue(spot.getLocation().getX() <= maxLatitude);

    }

    @Transactional
    @Test
    public void dataTablesSpotShouldReturnProperData() throws Exception {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user);
        }
        Map<String, Object> response = spotController.dataTablesSpot(0l, 5l, 1, "timestamp", "", "", "", "", 0, "asc",
                null);
        List<Spot> spots = (List<Spot>) response.get("aaData");
        Assert.assertEquals(5, spots.size());
    }

    @Transactional
    private User createUser(String login, String password) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setLogin(login);
        user.setEmail("john@doe.pl");

        Md5PasswordEncoder enc = new Md5PasswordEncoder();
        user.setPassword(enc.encodePassword(password, user.getLastName()));

        user = userRepository.save(user);
        return user;
    }

}
