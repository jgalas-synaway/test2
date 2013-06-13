package com.synaway.oneplaces.test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

import com.jayway.jsonpath.JsonPath;
import com.synaway.oneplaces.controller.rest.ControllerExceptionHandler;
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
	public void authenticationTest() throws Exception {
		createUser("john0", "password");
		AccessToken accessToken = userService.getToken("john0", "password");

		mockMvc.perform(
				post("/users/auth").param("login", "john0").param("password", "password")
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.token").value(accessToken.getToken()))
						.andExpect(jsonPath("$.expire", is(accessToken.getExpire().getTime())))
						.andExpect(jsonPath("$.user_id", equalTo(accessToken.getUserId().intValue())));
		
		
		
	}
	
	@Test
	public void getAllShouldReturnPropperData() throws Exception{
		User user = createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");

		mockMvc.perform(
				get("/users").param("access_token", accessToken.getToken())
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$").isArray())
						.andExpect(jsonPath("$.[0].firstName").value(user.getFirstName()))
						.andExpect(jsonPath("$.[0].lastName").value(user.getLastName()))
						.andExpect(jsonPath("$.[0].login").value(user.getLogin()))
						.andExpect(jsonPath("$.[0].password").doesNotExist())
						.andDo(print());
	}
	

	@Test
	public void getUserShouldReturnPropperData() throws Exception {
		User user = createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");
		exception.expect(NestedServletException.class);

		mockMvc.perform(
				get("/users/"+user.getId()).param("access_token", accessToken.getToken())
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.firstName").value(user.getFirstName()))
					.andExpect(jsonPath("$.lastName").value(user.getLastName()))
					.andExpect(jsonPath("$.login").value(user.getLogin()))
					.andExpect(jsonPath("$.password").doesNotExist())
						.andDo(print());

	}
	
	
	@Test
	public void updateUserShouldReturnPropperData() throws Exception{
		User user = createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");

		mockMvc.perform(
				put("/users/").param("access_token", accessToken.getToken())
				.content("{ \"id\":"+user.getId()+", \"firstName\":\"Gregory\", \"password\":\"pass\"}")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andDo(print());
		
		user = userRepository.findOne(user.getId());

		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		Assert.assertTrue("invalid password",enc.isPasswordValid(user.getPassword(), "pass", user.getLastName()));
		Assert.assertEquals("Gregory", user.getFirstName());
		
	}
	
	@Test
	public void meShouldReturnPropperData() throws Exception{
		User user = createUser("john", "password");
		AccessToken accessToken = userService.getToken("john", "password");

		mockMvc.perform(
				get("/users/me").param("access_token", accessToken.getToken())				
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.id").value(user.getId().intValue()))
						.andDo(print());
		
		
	}
	
	@Test
	public void getUserSpotsShouldReturnPropperData() throws Exception{
		User user = createUser("john", "password");
		
		Spot spot = new Spot();
		spot.setUser(user);
		spot = spotRepository.save(spot);
		
		AccessToken accessToken = userService.getToken("john", "password");
		
		mockMvc.perform(
				get("/users/"+user.getId()+"/spots").param("access_token", accessToken.getToken())				
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$").isArray())
						.andExpect(jsonPath("$.[0].spotId").value(spot.getId().intValue()))
						.andDo(print());
		
		mockMvc.perform(
				get("/users/"+user.getId()+"/spots").param("access_token", accessToken.getToken()).param("limit", "1").param("offset", "0")				
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$").isArray())
						.andExpect(jsonPath("$.[0].spotId").value(spot.getId().intValue()))
						.andDo(print());
	}
	
	
	@Test
	public void getUserLocationsShouldReturnPropperData() throws Exception{
		User user = createUser("john", "password");
		
		UserLocation location = new UserLocation();
		location.setUser(user);
		location = userLocationRepository.save(location);
		
		AccessToken accessToken = userService.getToken("john", "password");

		mockMvc.perform(
				get("/users/"+user.getId()+"/locations").param("access_token", accessToken.getToken())				
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$").isArray())
						.andExpect(jsonPath("$.[0].id").value(location.getId().intValue()))
						.andDo(print());
		
		mockMvc.perform(
				get("/users/"+user.getId()+"/locations").param("access_token", accessToken.getToken()).param("limit", "1").param("offset", "0")		
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$").isArray())
						.andExpect(jsonPath("$.[0].id").value(location.getId().intValue()))
						.andDo(print());
	}
	
	
	@Test
	public void addUserShouldReturnPropperData() throws Exception{

		mockMvc.perform(
				post("/users")
				.content("{ " +
							"\"id\":0, " +
							"\"firstName\":\"John\", " +
							"\"lastName\":\"Doe\", " +
							"\"login\":\"john\", " +
							"\"role\":\"user\", " +							
							"\"password\":\"password\"" +
						"}")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.error.code").value(500))
						.andDo(print());
		
		mockMvc.perform(
				post("/users")
				.content("{ " +
							"\"firstName\":\"John\", " +
							"\"lastName\":\"Doe\", " +
							"\"role\":\"user\", " +							
							"\"password\":\"password\"" +
						"}")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.error.code").value(500))
						.andDo(print());
		
		mockMvc.perform(
				post("/users")
				.content("{ " +
							"\"firstName\":\"John\", " +
							"\"lastName\":\"Doe\", " +
							"\"login\":\"john\", " +
							"\"role\":\"user\", " +		
						"}")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.error.code").value(500))
						.andDo(print());
		
		MvcResult result = mockMvc.perform(
				post("/users")
				.content("{ " +
							"\"firstName\":\"John\", " +
							"\"lastName\":\"Doe\", " +
							"\"login\":\"john\", " +
							"\"role\":\"user\", " +							
							"\"password\":\"password\"" +
						"}")
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.firstName").value("John"))
						.andExpect(jsonPath("$.lastName").value("Doe"))
						.andExpect(jsonPath("$.login").value("john"))
						.andDo(print()).andReturn();
		
		String json = result.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(json);
		
		User user = userRepository.findOne(jsonNode.get("id").asLong());

		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		Assert.assertTrue("invalid password",enc.isPasswordValid(user.getPassword(), "password", user.getLastName()));

		
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
