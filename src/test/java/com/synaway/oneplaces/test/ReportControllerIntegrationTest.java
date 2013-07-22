package com.synaway.oneplaces.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.time.DateUtils;
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

import com.synaway.oneplaces.controller.rest.ReportController;
import com.synaway.oneplaces.controller.rest.SpotController;
import com.synaway.oneplaces.dto.ActivityReportDTO;
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
public class ReportControllerIntegrationTest extends AbstractIntegrationTest {

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
    SpotService spotService;

    @Autowired
    UserLocationRepository userLocationRepository;

    @Autowired
    private ReportController reportController;

    @Before
    public void cleanDatabase() throws Exception {
        accessTokenRepository.deleteAllInBatch();
        spotRepository.deleteAllInBatch();
        userLocationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    public void testActivityReportActiveUsers() throws MissingServletRequestParameterException {
        Date now = new Date();
        User user = createUser("john", "password");
        User user2 = createUser("john2", "password");

        UserLocation location = new UserLocation();
        location.setUser(user);
        location.setTimestamp(DateUtils.addMinutes(now, -15));
        location = userLocationRepository.save(location);

        location = new UserLocation();
        location.setUser(user2);
        location.setTimestamp(DateUtils.addMinutes(now, -16));
        location = userLocationRepository.save(location);

        location = new UserLocation();
        location.setUser(user2);
        location.setTimestamp(DateUtils.addMinutes(now, -90));
        location = userLocationRepository.save(location);

        location = new UserLocation();
        location.setUser(user2);
        location.setTimestamp(DateUtils.addMinutes(now, -91));
        location = userLocationRepository.save(location);

        addSpot(user, now);
        addSpot(user, now);
        addSpot(user, now);
        addSpot(user, DateUtils.addMinutes(now, -360));

        ActivityReportDTO report = reportController.activityReport(DateUtils.addMinutes(now, -120),
                DateUtils.addMinutes(now, -60));
        ActivityReportDTO report2 = reportController.activityReport(DateUtils.addMinutes(now, -120),
                DateUtils.addMinutes(now, 0));
        ActivityReportDTO report3 = reportController.activityReport(DateUtils.addMinutes(now, -1200),
                DateUtils.addMinutes(now, -360));

        assertEquals(Long.valueOf(1), report.getActiveUsers());
        assertEquals(Long.valueOf(2), report2.getActiveUsers());
        assertEquals(Long.valueOf(0), report3.getActiveUsers());

        assertEquals(Long.valueOf(0), report.getGreenRedClickCount());
        assertEquals(Long.valueOf(3), report2.getGreenRedClickCount());
        assertEquals(Long.valueOf(1), report3.getGreenRedClickCount());
    }

    private Spot addSpot(User user, Date timestamp) {
        double minLatitude = 19.885;
        double maxLatitude = 19.991;
        double minLongitude = 50.0208;
        double maxLongitude = 50.0831;

        Spot spot = new Spot();
        spot.setTimestamp(timestamp);
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
