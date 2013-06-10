package com.synaway.oneplaces.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.UserService;


@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	private static final int ACCESS_TOKEN_PERSISTANCE_DAYS = 30;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	@Autowired
	private SpotRepository spotRepository;
	
	@Autowired
	private UserLocationRepository userLocationRepository;
	
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public User getUser(long id){
		return userRepository.findOne(id);
	}
	
	@Override
	public List<User> getAll(){
		return userRepository.findAll();
	}
	
	@Override
	public User saveUser(User user) throws UserException{	
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		
		User existingUser = userRepository.findOneByLogin(user.getLogin());
		if(existingUser != null){
			throw new UserException("User with login "+user.getLogin()+" already exist.", UserException.GENERAL_USER_EXIST);
		}

		user.setPassword(enc.encodePassword(user.getPassword(), user.getLastName()));
		
		return userRepository.save(user);
	}
	
	@Override
	public AccessToken getToken(String login, String password) throws UserException{
		
		User user = userRepository.findOneByLogin(login);
		if(user == null){
			throw new UserException("Invalid login", UserException.GENERAL_INVALID_LOGIN);
		}
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		
		if(!enc.isPasswordValid(user.getPassword(), password, user.getLastName())){
			throw new UserException("Invalid password", UserException.GENERAL_INVALID_PASSWORD);
		}
		
		List<AccessToken> accessTokens = accessTokenRepository.findByUserOrderByExpireDesc(user);
		AccessToken accessToken = null;
		
		if(accessTokens.size() > 0){
			accessToken = accessTokens.get(0);
		}
		
		if(accessToken == null || accessToken.getExpire().getTime() < new Date().getTime()){
			accessToken = new AccessToken();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, ACCESS_TOKEN_PERSISTANCE_DAYS);
			accessToken.setExpire(cal.getTime());
			accessToken.setUser(user);
			String token =  KeyGenerators.string().generateKey()+KeyGenerators.string().generateKey();
			accessToken.setToken(token);
			accessTokenRepository.save(accessToken);
		}
		
		return accessToken;
	}
	
	@Override
	public User getCurrentUser(){
		String token = request.getParameter("access_token");
		if(token == null){
			return null;
		}
		AccessToken accessToken = accessTokenRepository.findByToken(token);		
		return accessToken.getUser();
	}
	
	@Override
	public User updateUser(User user){
		User existing = userRepository.findOne(user.getId());
		if(user.getPassword() != null){
			Md5PasswordEncoder enc = new Md5PasswordEncoder();
			existing.setPassword(enc.encodePassword(user.getPassword(), existing));
		}
		if(user.getFirstName() != null){
			existing.setFirstName(user.getFirstName());
		}
		if(user.getLastName() != null){
			existing.setLastName(user.getLastName());
		}
		if(user.getLogin() != null){
			existing.setLogin(user.getLogin());
		}
		if(user.getEmail() != null){
			existing.setEmail(user.getEmail());
		}
		if(user.getRole() != null){
			existing.setRole(user.getRole());
		}
		return userRepository.save(existing);
		
	}
	
	@Override
	public User delete(Long id){
		User user = userRepository.findOne(id);
		accessTokenRepository.delete(accessTokenRepository.findByUser(user));
		spotRepository.delete(spotRepository.findByUser(user));
		userLocationRepository.delete(userLocationRepository.findByUser(user));
		
		userRepository.delete(id);
		
		return user;
		
	}
	
	
	
}
