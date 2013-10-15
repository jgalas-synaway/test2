package com.synaway.oneplaces.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.dto.ReportParamsDTO;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.service.ReportService;

/**
 * Exposes API for reports.
 * 
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping(method = RequestMethod.POST, value = "/activity", produces = "application/json")
    @ResponseBody
    public ActivityReportDTO activityReport(@RequestBody final ReportParamsDTO params) {
        return reportService.activityReport(params);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activity/map/counters/{zoom}/{x}/{y}.json", produces = "application/json")
    @ResponseBody
    public ActivityReportDTO activityReportCountersMap(@RequestBody final ReportParamsDTO params, @PathVariable int zoom,
            @PathVariable int x, @PathVariable int y) {
        return reportService.activityReport(params, zoom, x, y);
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/activity/map/pins/{zoom}/{x}/{y}.json", produces = "application/json")
    @ResponseBody
    public List<Spot> activityReportPinMap(@RequestBody final ReportParamsDTO params, @PathVariable int zoom,
            @PathVariable int x, @PathVariable int y) {
        return reportService.getSpots(params, zoom, x, y);
    }
}
