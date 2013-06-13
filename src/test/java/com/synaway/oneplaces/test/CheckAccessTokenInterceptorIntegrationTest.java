package com.synaway.oneplaces.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.synaway.oneplaces.exception.AccessTokenException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CheckAccessTokenInterceptorIntegrationTest extends AbstractIntegrationTest {
	private static Logger logger = Logger.getLogger(CheckAccessTokenInterceptorIntegrationTest.class);

	@Autowired
	WebApplicationContext ctx;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	AccessTokenRepository accessTokenRepository;	

	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	UserLocationRepository userLocationRepository;
	

	MockMvc mockMvc;
	
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

	}

	@Before
	public void cleanDatabase() {
		accessTokenRepository.deleteAllInBatch();
		spotRepository.deleteAllInBatch();
		userLocationRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}
	

	@Test
	public void unauthorizedTest() throws Exception {

		mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.error.code").value(301)).andDo(print());

	}
	
	@Test
	public void frontrndResourcesTest() throws Exception {

		mockMvc.perform(
				get("/frontend"))
				.andExpect(status().isOk());
		
		mockMvc.perform(
				get("/resources"))
				.andExpect(status().isNotFound()).andDo(print());

	}
	
	@Test
	public void invalidAccesToken() throws Exception {

		mockMvc.perform(
			get("/users").accept(MediaType.APPLICATION_JSON).param("access_token", "invalidAccesToken"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.error.code").value(AccessTokenException.GENERAL_ACCESS_TOKEN_INVALID))
			.andDo(print());

	}
	
	
	@Test
	public void expiredAccesToken() throws Exception {
		createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");
		Calendar cal = Calendar.getInstance();
		cal.setTime(accessToken.getExpire());
		cal.add(Calendar.DATE, -30);
		accessToken.setExpire(cal.getTime());
		accessTokenRepository.save(accessToken);
		
		mockMvc.perform(
			get("/users").accept(MediaType.APPLICATION_JSON).param("access_token", accessToken.getToken()))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.error.code").value(AccessTokenException.GENERAL_ACCESS_TOKEN_EXPIRED))
			.andDo(print());

	}
	
	@Transactional
	private User createUser(String login, String password)  {
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
