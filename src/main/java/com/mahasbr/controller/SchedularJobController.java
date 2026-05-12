package com.mahasbr.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class SchedularJobController {
	
	private static final Logger logger = LoggerFactory.getLogger(SchedularJobController.class);
	
	 @Autowired
	    private ThreadPoolTaskScheduler taskScheduler;

	 @Scheduled(cron = "0 0 0 * * ?")  // Runs at midnight every day
	    public void fetchAndProcessData() {
	        taskScheduler.execute(() -> {
	        	
	        	System.out.println("hiiiii");
});
}}