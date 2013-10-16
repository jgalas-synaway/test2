package com.synaway.oneplaces.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/frontend")
public class FrontendController {
	
    @Value("${mobileRelease.manifestUrl}")
    private String mobileReleaseManifestUrl;
    
	@RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping(method = RequestMethod.GET, value="/mobile")
    public ModelAndView getManifestForIOS() {
        return new ModelAndView("mobile", "manifestUrl", mobileReleaseManifestUrl);
    }
}
