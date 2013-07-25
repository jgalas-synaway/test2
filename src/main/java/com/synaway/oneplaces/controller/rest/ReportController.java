package com.synaway.oneplaces.controller.rest;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.dto.ActivityReportDTO;
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

    @RequestMapping(method = RequestMethod.GET, value = "/activity", produces = "application/json")
    @ResponseBody
    public ActivityReportDTO activityReport(
            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fromDate,
            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date toDate) {
        return reportService.activityReport(fromDate, toDate);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activity/map/{zoom}/{x}/{y}.json", produces = "application/json")
    @ResponseBody
    public ActivityReportDTO activityReportMap(
            @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date fromDate,
            @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date toDate,
            @PathVariable int zoom, @PathVariable int x, @PathVariable int y) {
        return reportService.activityReport(fromDate, toDate, zoom, x, y);
    }
}
