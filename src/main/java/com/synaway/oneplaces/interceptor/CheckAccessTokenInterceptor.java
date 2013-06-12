package com.synaway.oneplaces.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.synaway.oneplaces.exception.AccessTokenException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.repository.AccessTokenRepository;

public class CheckAccessTokenInterceptor implements HandlerInterceptor {
	
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AccessTokenException {
		String uri = request.getRequestURI().replace(request.getContextPath(), "");

		
		if(uri.startsWith("/frontend") || uri.startsWith("/resources")){
			return true;
		}
		
		if(uri.endsWith("/users") && request.getMethod().equalsIgnoreCase("POST")){
			return true;
		}
		
		if(uri.endsWith("/users/auth")){
			return true;
		}
		
		String token = request.getParameter("access_token");
		
		if(token == null){
			throw new AccessTokenException("An access token is required to request this resource.", AccessTokenException.GENERAL_ACCESS_TOKEN_REQUIRED);
		}
		
		AccessToken accessToken = accessTokenRepository.findByToken(token);		
		
		if(accessToken == null){
			throw new AccessTokenException("Invalid access token signature.", AccessTokenException.GENERAL_ACCESS_TOKEN_INVALID);
		}
		
		if(accessToken.getExpire().getTime() < new Date().getTime()){
			throw new AccessTokenException("Access token has expired.", AccessTokenException.GENERAL_ACCESS_TOKEN_EXPIRED);
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
	}

}
