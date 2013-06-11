package com.synaway.oneplaces.test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

import com.synaway.oneplaces.controller.rest.ControllerExceptionHandler;
import com.synaway.oneplaces.controller.rest.UserController;
import com.synaway.oneplaces.exception.AccessTokenException;
import com.synaway.oneplaces.exception.GeneralException;
import com.synaway.oneplaces.interceptor.CheckAccessTokenInterceptor;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.UserService;
import com.synaway.oneplaces.service.impl.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserControllerIntegrationTest extends AbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(UserControllerIntegrationTest.class);

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

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

	}

	@Before
	public void cleanDatabase() throws NoSuchAlgorithmException {
		accessTokenRepository.deleteAllInBatch();
		spotRepository.deleteAllInBatch();
		userLocationRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	public void unauthorizedTest() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(), new UserServiceImpl())
				.addInterceptors(new CheckAccessTokenInterceptor()).build();
		try {
			mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON));
		} catch (Exception e) {
			Assert.assertEquals(AccessTokenException.class, e.getCause().getClass());
		}
	}

	@Test
	public void authenticationTest() throws Exception {
		createUser("john0", "password");
		AccessToken accessToken = userService.getToken("john0", "password");

		mockMvc.perform(
				post("/users/auth").param("login", "john0").param("password", "password")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.token").value(accessToken.getToken()))
						.andExpect(jsonPath("$.expire", is(accessToken.getExpire().getTime())))
						.andExpect(jsonPath("$.user_id", equalTo(accessToken.getUserId().intValue())))
						.andDo(print());

	}
	
	
	
	

	@Transactional
	private User createUser(String login, String password) throws NoSuchAlgorithmException {
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
