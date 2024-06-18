package com.mahasbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class SchedularJobController {
	
	 @Autowired
	    private ThreadPoolTaskScheduler taskScheduler;

	 @Scheduled(cron = "0 0 0 * * ?")  // Runs at midnight every day
	    public void fetchAndProcessData() {
	        taskScheduler.execute(() -> {
	        	
	        	System.out.println("hiiiii");
});
}}