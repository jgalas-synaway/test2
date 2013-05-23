package com.synaway.oneplaces.test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.UserRepository;
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
	
	private List<User> users = null;
	
	@Before
	public void cleanDatabase() throws NoSuchAlgorithmException{
		accessTokenRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		users = new ArrayList<User>();
		for(int i = 0; i < 20; i++){
			users.add(createUser("john"+i, "password"));
		}
	}

	@Test
	public void getTokenShouldReturnPropperData() throws Exception{
		Calendar exipireCal = Calendar.getInstance();
		Calendar calNextMonth = Calendar.getInstance();
		calNextMonth.add(Calendar.DATE, 30);
		Calendar calYesterday = Calendar.getInstance();
		calYesterday.add(Calendar.DATE, -1);
		
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
	
	
	private User createUser(String login, String password) throws NoSuchAlgorithmException{
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword(password);
		user.setLogin(login);
		user.setEmail("john@doe.pl");		
		user = userService.saveUser(user);
		return user;
	}
	
}
