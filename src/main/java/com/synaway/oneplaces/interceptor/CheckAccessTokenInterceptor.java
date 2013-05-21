package com.synaway.oneplaces.interceptor;

import java.io.BufferedReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.synaway.oneplaces.exception.AccessTokenException;
import com.synaway.oneplaces.exception.BadRequestParametersException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.repository.AccessTokenRepository;

public class CheckAccessTokenInterceptor implements HandlerInterceptor {

	private static Logger logger = Logger.getLogger(CheckAccessTokenInterceptor.class);
	
	@Autowired
	AccessTokenRepository accessTokenRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String uri = request.getRequestURI().replace(request.getContextPath(), "");

		//logger.info(request.getParameter("login"));
	     
		
		
		if(uri.endsWith("/users") && request.getMethod().equalsIgnoreCase("POST")){
			return true;
		}
		
		if(uri.endsWith("/users/auth")){
			return true;
		}
		
		String token = request.getParameter("access_token");
		
		if(token == null){
			throw new AccessTokenException("An access token is required to request this resource.", 501);
		}
		
		AccessToken accessToken = accessTokenRepository.findByToken(token);		
		
		if(accessToken == null){
			throw new AccessTokenException("Invalid access token signature.", 502);
		}
		
		if(accessToken.getExpire().getTime() < new Date().getTime()){
			throw new BadRequestParametersException("Access token has expired.", 503);
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
