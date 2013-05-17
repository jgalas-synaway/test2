package com.synaway.oneplaces.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;

public interface UserService {

	User getUser(long id);

	List<User> getAll();

	User saveUser(User spot) throws NoSuchAlgorithmException;

	AccessToken getToken(String login, String password) throws Exception;

}
