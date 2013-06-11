package com.synaway.oneplaces.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

import com.synaway.oneplaces.controller.rest.ControllerExceptionHandler;
import com.synaway.oneplaces.controller.rest.SpotController;
import com.synaway.oneplaces.controller.rest.UserController;
import com.synaway.oneplaces.exception.AccessTokenException;
import com.synaway.oneplaces.exception.GeneralException;
import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.interceptor.CheckAccessTokenInterceptor;
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
import com.synaway.oneplaces.service.impl.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class SpottControllerIntegrationTest extends AbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(SpottControllerIntegrationTest.class);


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


	@Before
	public void cleanDatabase() throws Exception {
		accessTokenRepository.deleteAllInBatch();
		spotRepository.deleteAllInBatch();
		userLocationRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		
		
	}
	
	@Test
	public void getAllSpotsShouldReturnProperDate() throws MissingServletRequestParameterException{
		User user = createUser("john", "password");
		for(int i = 0; i < 10; i++){
			addSpot(user);
		}
		
		List<Spot> spots = spotController.getAllSpots(null, null, null, false);		
		Assert.assertEquals(10, spots.size());
		
		spots = spotController.getAllSpots(0.0, 0.0, 100, false);
		Assert.assertEquals(0, spots.size());
		
		spots = spotController.getAllSpots(50.05, 19.7, 30000, false);
		Assert.assertEquals(10, spots.size());		
	}
	
	@Test (expected = MissingServletRequestParameterException.class)
	public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionLatitude() throws MissingServletRequestParameterException{
		spotController.getAllSpots(null, 19.7, 20000, false);	
	}
	
	@Test (expected = MissingServletRequestParameterException.class)
	public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionLongitude() throws MissingServletRequestParameterException{
		spotController.getAllSpots(50.05, null, 20000, false);	
	}

	
	@Test (expected = MissingServletRequestParameterException.class)
	public void getAllSpotsShouldThrowMissingServletRequestParameterExceptionRadius() throws MissingServletRequestParameterException{
		spotController.getAllSpots(50.05, 19.7,  null, false);	
	}


	@Transactional
	@Test
	public void addSpotShouldReturnProperData() throws Exception {
		User user = createUser("john", "password");

		Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
		
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
	public void updateSpotShouldReturnProperData() throws Exception  {
		User user = createUser("john", "password");

		Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
		spot = spotController.updateSpot("{\"longitude\": 19.756278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"spotId\" : "+spot.getId()+", \"status\" : \"somestatus\" }");
		
		Assert.assertEquals(19.756278, spot.getLocation().getX(), 0);
		Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
		Assert.assertEquals(user.getId(), spot.getUser().getId());

	}
	
	@Transactional
	@Test(expected = MissingServletRequestParameterException.class)
	public void updateSpotShouldThrow() throws Exception {
		User user = createUser("john", "password");

		Spot spot = spotController.updateSpot("{\"longitude\": 19.756278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
		
		Assert.assertEquals(19.756278, spot.getLocation().getX(), 0);
		Assert.assertEquals(50.06063, spot.getLocation().getY(), 0);
		Assert.assertEquals(user.getId(), spot.getUser().getId());

	}

	@Transactional
	@Test
	public void getSpotTest() throws MissingServletRequestParameterException, UserException, IOException {
		User user = createUser("john", "password");

		Spot spot = spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
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
		
		for(int i = 0; i < 10; i++){
			spotController.addSpot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
		}
		
		for(int i = 0; i < 10; i++){
			spotController.addSpot("{\"longitude\": 18.856278, \"latitude\": 58.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
		}
		
		Assert.assertEquals(20 ,spotController.getAllSpots(null, null,null, null).size());
		Assert.assertEquals(10 ,spotController.getAllSpots(50.06063, 19.856278,100, null).size());
		Assert.assertEquals(10 ,spotController.getAllSpots(58.06063, 18.856278,100, null).size());

	}
	
	@Transactional
	@Test
	public void userTrackingTest() throws Exception{
		User user = createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");
		
		request.addParameter("access_token", accessToken.getToken());
		
		for(int i = 0; i < 10; i++){
			spotController.getAllSpots(50.06063, 19.856278,100, null);
		}
		
		Assert.assertEquals(10 ,userLocationRepository.count());
		List<UserLocation> list = userLocationRepository.findByUserOrderByTimestampDesc(user, new PageRequest(0, 10));
		Iterator<UserLocation> it = list.iterator();
		
		Date previous = new Date();
		while (it.hasNext()) {
			UserLocation userLocation = (UserLocation) it.next();
			Assert.assertTrue("wrong order of user locations",previous.getTime() >= userLocation.getTimestamp().getTime());
			Assert.assertEquals(19.856278, userLocation.getLocation().getX(), 0);
			Assert.assertEquals(50.06063, userLocation.getLocation().getY(), 0);
			
			previous = userLocation.getTimestamp();
			
		}
		
		
	}
	
	@Transactional
	@Test
	public void setOccupySpotShouldReturnProperData()  {
		User user = createUser("john", "password");
		Spot spot = addSpot(user);

		Assert.assertEquals("free", spotService.getSpot(spot.getId()).getStatus());
		
		spotController.setOccupy(spot.getId());
		
		Assert.assertEquals("occupied", spotService.getSpot(spot.getId()).getStatus());
		
	}
	
	@Transactional
	@Test
	public void deleteSpotShouldReturnProperData() throws Exception  {
		User user = createUser("john", "password");
		Spot spot = addSpot(user);
		spotController.deleteSpot(spot.getId());
		Assert.assertNull(spotService.getSpot(spot.getId()));
	}
	
	
	private Spot addSpot(User user){
		double minLatitude = 19.885;
		double maxLatitude = 19.991;
		double minLongitude = 50.0208;
		double maxLongitude = 50.0831;
		
		Spot spot = new Spot();
		spot.setTimestamp(new Date());
		spot.setUser(userService.getAll().get(0));
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
