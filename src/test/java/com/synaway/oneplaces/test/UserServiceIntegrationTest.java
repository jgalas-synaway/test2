package com.synaway.oneplaces.test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
public class UserServiceIntegrationTest extends AbstractIntegrationTest {
	
	@Autowired
	UserService userService;
	
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
	
	private List<User> users = null;
	
	@Before
	public void cleanDatabase() throws NoSuchAlgorithmException, UserException{
		accessTokenRepository.deleteAllInBatch();
		spotRepository.deleteAllInBatch();
		userLocationRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		
	}
	
	@Test
	public void saveUserShouldReturnPropperData() throws UserException{
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("password");
		user.setLogin("login");
		user.setEmail("john@doe.pl");		
		
		User saved = userService.saveUser(user);
		
		user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("password");
		user.setLogin("login");
		user.setEmail("john@doe.pl");

		Assert.assertEquals(user.getFirstName(), saved.getFirstName());
		Assert.assertEquals(user.getLastName(), saved.getLastName());
		Assert.assertEquals(user.getLogin(), saved.getLogin());
		Assert.assertEquals(user.getEmail(), saved.getEmail());
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		Assert.assertTrue("invalid passsword", enc.isPasswordValid(saved.getPassword(), user.getPassword(), user.getLastName()));
		
	}
	
	@Test(expected=UserException.class)
	public void saveUserShouldThrowUserException() throws UserException{
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("password");
		user.setLogin("login");
		user.setEmail("john@doe.pl");		
		
		userService.saveUser(user);
		userService.saveUser(user);
		
	}

	@Test
	public void getTokenShouldReturnPropperData() throws Exception{
		Calendar exipireCal = Calendar.getInstance();
		Calendar calNextMonth = Calendar.getInstance();
		calNextMonth.add(Calendar.DATE, 30);
		Calendar calYesterday = Calendar.getInstance();
		calYesterday.add(Calendar.DATE, -1);
		
		users = new ArrayList<User>();
		for(int i = 0; i < 20; i++){
			users.add(createUser("john"+i, "password"));
		}
		
		Iterator<User> it = users.iterator();		
		while (it.hasNext()) {
			User user = (User) it.next();
			AccessToken accessToken = userService.getToken(user.getLogin(), "password");

			exipireCal.setTime(accessToken.getExpire());
			
			Assert.assertEquals("Invalid user id" ,accessToken.getUserId(), user.getId());
			Assert.assertEquals("Invalid expire date" ,exipireCal.get(Calendar.DATE) ,  calNextMonth.get(Calendar.DATE) );
			
			String token = accessToken.getToken();
			accessToken = userService.getToken(user.getLogin(), "password");
			
			Assert.assertEquals("Invalid token", accessToken.getToken(), token);
			
			accessToken.setExpire(calYesterday.getTime());
			accessTokenRepository.save(accessToken);
			
			accessToken = userService.getToken(user.getLogin(), "password");
			
			Assert.assertNotSame("New token not generated", token, accessToken.getToken());
			Assert.assertEquals("Invalid user id" ,accessToken.getUserId(), user.getId());
			
			exipireCal.setTime(accessToken.getExpire());
			
			Assert.assertEquals("Invalid expire date" ,exipireCal.get(Calendar.DATE) ,  calNextMonth.get(Calendar.DATE) );
		}
		
	}
	
	@Test(expected=UserException.class)
	public void getTokenShouldThrowUserExceptionLogin() throws UserException{
		userService.getToken("john", "password");
	}
	
	@Test(expected=UserException.class)
	public void getTokenShouldThrowUserExceptionPassword() throws UserException{
		User user =  createUser("john", "password");
		userService.getToken("john", "invalidPassword");
	}
	
	@Test
	public void getCurrentUserShouldReturnPropperData() throws UserException{
		Assert.assertNull(userService.getCurrentUser());
		
		request.setParameter("access_token", "someInvalidToken");
		Assert.assertNull(userService.getCurrentUser());
		
		User user =  createUser("john", "password");
		AccessToken token = userService.getToken(user.getLogin(), "password");
		request.setParameter("access_token", token.getToken());
		
		User current = userService.getCurrentUser();
		
		Assert.assertEquals(user.getId(), current.getId());
	}
	
	
	@Test
	public void updateUserShouldReturnPropperData() throws MissingServletRequestParameterException{
		User userBefore = createUser("john1", "password");
		
		
		
		User userUpdate = new User();
		userUpdate.setId(userBefore.getId());
		
		User userAfter = userService.updateUser(userUpdate);

		Assert.assertEquals(userBefore.getFirstName(), userAfter.getFirstName());
		Assert.assertEquals(userBefore.getLastName(), userAfter.getLastName());
		Assert.assertEquals(userBefore.getLogin(), userAfter.getLogin());
		Assert.assertEquals(userBefore.getEmail(), userAfter.getEmail());
		Assert.assertEquals(userBefore.getRole(), userAfter.getRole());
		Assert.assertEquals(userBefore.getPassword(), userAfter.getPassword());
		
		userUpdate.setPassword("somePassword");
		userUpdate.setFirstName("Gregory");
		userUpdate.setLastName("House");
		
		userAfter = userService.updateUser(userUpdate);
		
		Assert.assertEquals(userUpdate.getFirstName(), userAfter.getFirstName());
		Assert.assertEquals(userUpdate.getLastName(), userAfter.getLastName());
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		Assert.assertTrue("invalid passsword", enc.isPasswordValid(userAfter.getPassword(), "somePassword", userUpdate.getLastName()));
	}
	
	@Test
	public void deleteUserShouldReturnPropperData(){
		User user = createUser("john1", "password");
		
		Assert.assertEquals(1, userRepository.count());
		
		userService.delete(user.getId());
		
		Assert.assertEquals(0, userRepository.count());
	}
	
	private User createUser(String login, String password){
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword(password);
		user.setLogin(login);
		user.setEmail("john@doe.pl");		
		try {
			user = userService.saveUser(user);
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
}
