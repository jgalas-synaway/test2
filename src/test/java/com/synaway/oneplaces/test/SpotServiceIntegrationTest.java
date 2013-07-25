package com.synaway.oneplaces.test;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SpotServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    UserService userService;

    @Autowired
    SpotService spotService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Autowired
    SpotRepository spotRepository;

    @Autowired
    UserLocationRepository userLocationRepository;

    @Autowired
    private MockHttpServletRequest request;

    @Before
    public void cleanDatabase() throws Exception {
        accessTokenRepository.deleteAllInBatch();
        spotRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        userLocationRepository.deleteAllInBatch();

    }

    @Transactional
    @Test
    public void getSpotShouldReturnPropperData() {
        User user = createUser("john", "password");

        Spot spot = new Spot();
        spot.setLocation(spotService.createPoint(19.856278, 50.06063));
        spot.setUser(user);

        spot = spotRepository.save(spot);

        spot = spotService.getSpot(spot.getId());

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
    public void getAllShouldReturnPropperData() {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user);
        }

        List<Spot> spots = spotService.getAll();
        Assert.assertEquals(10, spots.size());

    }

    @Transactional
    @Test
    public void getAllParamShouldReturnPropperData() {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user);
        }

        List<Spot> spots = spotService.getAll("timestamp", "asc", 2, 5);
        Assert.assertEquals(5, spots.size());
        Iterator<Spot> it = spots.iterator();
        Spot prev = null;
        while (it.hasNext()) {
            Spot spot = (Spot) it.next();
            if (prev != null) {
                Assert.assertTrue("wrong order of entities", spot.getTimestamp().getTime() >= prev.getTimestamp()
                        .getTime());
            }
            prev = spot;
        }

        spots = spotService.getAll("timestamp", "desc", 3, 6);
        Assert.assertEquals(6, spots.size());
        it = spots.iterator();
        prev = null;
        while (it.hasNext()) {
            Spot spot = (Spot) it.next();
            if (prev != null) {
                Assert.assertTrue("wrong order of entities", spot.getTimestamp().getTime() <= prev.getTimestamp()
                        .getTime());
            }
            prev = spot;
        }

        spots = spotService.getAll("status", "asc", 2, 5);
        Assert.assertEquals(5, spots.size());

        spots = spotService.getAll("status", "asc", 3, 4);
        Assert.assertEquals(4, spots.size());
    }

    @Transactional
    @Test
    public void saveSpotShouldReturnPropperData() throws UserException {
        User user = createUser("john", "password");
        Spot spot = new Spot();
        spot.setLocation(spotService.createPoint(19.856278, 50.06063));
        spot.setUser(user);

        Spot spot2 = spotService.saveSpot(spot);
        Assert.assertEquals(spot.getTimestamp(), spot2.getTimestamp());
        Assert.assertEquals(spot.getUser(), spot2.getUser());
        Assert.assertEquals(spot.getLocation(), spot2.getLocation());

        AccessToken token = userService.getToken(user.getLogin(), "password");
        request.setParameter("access_token", token.getToken());

        Spot spot3 = new Spot();
        spot3.setLocation(spotService.createPoint(19.856278, 50.06063));

        Spot spot4 = spotService.saveSpot(spot3);
        Assert.assertEquals(spot3.getTimestamp(), spot4.getTimestamp());
        Assert.assertEquals(spot3.getUser().getId(), spot4.getUser().getId());
        Assert.assertEquals(spot3.getLocation(), spot4.getLocation());

    }

    @Transactional
    @Test
    public void getByUserShouldReturnPropperData() {

        User user1 = createUser("john1", "password");
        User user2 = createUser("john3", "password");

        for (int i = 0; i < 5; i++) {
            addSpot(user1);
        }
        for (int i = 0; i < 10; i++) {
            addSpot(user2);
        }
        Assert.assertEquals(5, spotService.getByUser(user1, 20, 0).size());
        Assert.assertEquals(10, spotService.getByUser(user2, 20, 0).size());
    }

    @Transactional
    @Test
    public void getByLatitudeLongitudeAndRadiusShouldReturnPropperData() {
        User user = createUser("john", "password");
        for (int i = 0; i < 10; i++) {
            addSpot(user);
        }

        List<Spot> spots = spotService.getByLatitudeLongitudeAndRadius(0.0, 0.0, 100);
        Assert.assertEquals(0, spots.size());

        spots = spotService.getByLatitudeLongitudeAndRadius(50.05, 19.7, 30000);
        Assert.assertEquals(10, spots.size());
    }

    @Transactional
    @Test(expected = MissingServletRequestParameterException.class)
    public void json2SpotShouldThrowMissingServletRequestParameterExceptionLongitude()
            throws MissingServletRequestParameterException, UserException, IOException {
        User user = createUser("john", "password");

        Spot spot = spotService.json2Spot("{\"latitude\": 50.06063, \"userId\" : " + user.getId()
                + ", \"status\" : \"somestatus\" }");
    }

    @Transactional
    @Test(expected = MissingServletRequestParameterException.class)
    public void json2SpotShouldThrowMissingServletRequestParameterExceptionLatitude()
            throws MissingServletRequestParameterException, UserException, IOException {
        User user = createUser("john", "password");
        Spot spot = spotService.json2Spot("{\"longitude\": 19.856278, \"userId\" : " + user.getId()
                + ", \"status\" : \"somestatus\" }");
    }

    @Transactional
    @Test(expected = MissingServletRequestParameterException.class)
    public void json2SpotShouldThrowMissingServletRequestParameterExceptionStatus()
            throws MissingServletRequestParameterException, UserException, IOException {
        User user = createUser("john", "password");
        Spot spot = spotService.json2Spot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "
                + user.getId() + " }");
    }

    @Transactional
    @Test(expected = UserException.class)
    public void json2SpotShouldThrowUserException() throws MissingServletRequestParameterException, UserException,
            IOException {

        Spot spot = spotService
                .json2Spot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : 0, \"status\" : \"somestatus\" }");
    }

    @Transactional
    @Test
    public void updateSpotShouldReturnPropperData() {
        User user = createUser("john1", "password");
        User user2 = createUser("john2", "password");
        Spot spotBefore = addSpot(user);

        Spot spotUpdate = new Spot();
        spotUpdate.setId(spotBefore.getId());

        Spot spotAfter = spotService.updateSpot(spotUpdate);

        Assert.assertEquals(spotBefore.getId(), spotAfter.getId());
        Assert.assertEquals(spotBefore.getLocation(), spotAfter.getLocation());
        Assert.assertEquals(spotBefore.getStatus(), spotAfter.getStatus());

        spotBefore = addSpot(user);

        spotUpdate = new Spot();
        spotUpdate.setId(spotBefore.getId());
        spotUpdate.setLocation(spotService.createPoint(19.0, 50.0));
        spotUpdate.setUser(user2);
        spotUpdate.setStatus("occupcied");

        spotAfter = spotService.updateSpot(spotUpdate);

        Assert.assertEquals(spotUpdate.getId(), spotAfter.getId());
        Assert.assertEquals(spotUpdate.getLocation(), spotAfter.getLocation());
        Assert.assertEquals(spotUpdate.getStatus(), spotAfter.getStatus());

    }

    protected Spot addSpot(User user) {
        double minLatitude = 19.885;
        double maxLatitude = 19.991;
        double minLongitude = 50.0208;
        double maxLongitude = 50.0831;

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
