package com.synaway.oneplaces.service.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.UserService;


@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AccessTokenRepository accessTokenRepository;
	
	@Override
	public User getUser(long id){
		return userRepository.findOne(id);
	}
	
	@Override
	public List<User> getAll(){
		return userRepository.findAll();
	}
	
	@Override
	public User saveUser(User user) throws NoSuchAlgorithmException{	
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		user.setPassword(enc.encodePassword(user.getPassword(), null));		
		return userRepository.save(user);
	}
	
	@Override
	public AccessToken getToken(String login, String password) throws Exception{
		
		User user = userRepository.findOneByLogin(login);
		if(user == null){
			throw new Exception("invalid login");
		}
		Md5PasswordEncoder enc = new Md5PasswordEncoder();
		
		if(!enc.isPasswordValid(user.getPassword(), password, null)){
			throw new Exception("invalid password");
		}
		
		AccessToken accessToken = accessTokenRepository.findByUserOrderByExpireDesc(user);
		
		if(accessToken == null || accessToken.getExpire().getTime() < new Date().getTime()){
			accessToken = new AccessToken();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 30);
			accessToken.setExpire(cal.getTime());
			accessToken.setUser(user);
			String token =  KeyGenerators.string().generateKey()+KeyGenerators.string().generateKey();
			accessToken.setToken(token);
			accessTokenRepository.save(accessToken);
		}
		
		return accessToken;
	}
	
	
	
}
