package com.synaway.oneplaces.test;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

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

import com.synaway.oneplaces.controller.rest.SpotController;
import com.synaway.oneplaces.exception.UserException;
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
	
	@Before
	public void cleanDatabase() throws Exception {
		accessTokenRepository.deleteAllInBatch();
		spotRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		userLocationRepository.deleteAllInBatch();
		
		
	}
	
	
	@Transactional
	@Test(expected=MissingServletRequestParameterException.class)
	public void json2SpotShouldThrowMissingServletRequestParameterExceptionLongitude() throws MissingServletRequestParameterException, UserException, IOException  {
		User user = createUser("john", "password");

		Spot spot = spotService.json2Spot("{\"latitude\": 50.06063, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
	}
	
	@Transactional
	@Test(expected=MissingServletRequestParameterException.class)
	public void json2SpotShouldThrowMissingServletRequestParameterExceptionLatitude() throws MissingServletRequestParameterException, UserException, IOException  {
		User user = createUser("john", "password");
		Spot spot = spotService.json2Spot("{\"longitude\": 19.856278, \"userId\" : "+user.getId()+", \"status\" : \"somestatus\" }");
	}

	
	@Transactional
	@Test(expected=MissingServletRequestParameterException.class)
	public void json2SpotShouldThrowMissingServletRequestParameterExceptionStatus() throws MissingServletRequestParameterException, UserException, IOException  {
		User user = createUser("john", "password");
		Spot spot = spotService.json2Spot("{\"longitude\": 19.856278, \"latitude\": 50.06063, \"userId\" : "+user.getId()+" }");
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
