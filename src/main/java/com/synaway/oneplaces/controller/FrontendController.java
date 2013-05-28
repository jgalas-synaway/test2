package com.synaway.oneplaces.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/frontend")
public class FrontendController {
	
	@RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        return model;
    }

}
