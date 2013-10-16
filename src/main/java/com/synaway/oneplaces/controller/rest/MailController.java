package com.synaway.oneplaces.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.service.MailService;

@Controller
@RequestMapping("/mails")
public class MailController {
    
    @Autowired
    private MailService mailService;
        
    @Value("${mobileRelease.siteUrl}")
    private String mobileReleaseSiteUrl;
 

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
    @ResponseBody
    public void sendMobileAppReleaseMail(HttpServletRequest request, @RequestParam(value = "userId") Long userId) throws UserException {
        mailService.sendMobileAppReleaseMail(userId, mobileReleaseSiteUrl);
    }
    
}
