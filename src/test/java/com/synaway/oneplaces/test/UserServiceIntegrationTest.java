package com.synaway.oneplaces.test;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceIntegrationTest extends AbstractIntegrationTest {
	
	@Autowired
	UserService userService;

	@Test
	public void getTokenShouldReturnPropperData() throws NoSuchAlgorithmException{
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("password");
		user.setLogin("john");
		user.setEmail("john@doe.pl");
		
		userService.saveUser(user);
		
		//userService.getToken(login, password);
	}
}
