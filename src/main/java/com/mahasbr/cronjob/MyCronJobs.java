package com.mahasbr.cronjob;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mahasbr.entity.DetailsPage;

//@Component
public class MyCronJobs {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final long DELAY = 1000 * 60 * 30; // 30 minutes in milliseconds
    
    @Value("${api.base-url}") // Reads base URL from application.properties or application.yml
    private String baseUrl;


    // Job 1: Runs every 30 minutes starting from 12:00 AM
    @Scheduled(cron = "0 0 0/1 * * ?") // Runs every hour
    public void job1() {
        executeJob(1);
    }

    private void executeJob(int jobNumber) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
            
            
            List<DetailsPage> lstDetailsPage=new ArrayList<>();

            lstDetailsPage= webClient.get()
                    .uri("/sbr.php")
                    .retrieve()
                    .bodyToFlux(DetailsPage.class) // Corrected deserialization
                    .collectList()
                    .block(); // Blocking operation, consider using reactive chaining
            // Your logic for the job
            System.out.println("Job " + jobNumber + " completed."+lstDetailsPage.toString());

            if(lstDetailsPage.size()>0) {
            	//brn logic
            	Iterator itr=lstDetailsPage.iterator();
            	String brnNumber=null;
            	String villageCode=null;
            	String wardNumber=null;
            	String runningSeq=null;
                while(itr.hasNext()) {
                	DetailsPage dt=(DetailsPage) itr.next();
                	villageCode=dt.getTownVillage();
                	wardNumber=dt.getHoLocality();
                	
                	 Random random = new Random();
                     int randomNumber = random.nextInt(9000) + 1000;
                	
                	brnNumber=villageCode.trim()+wardNumber.trim()+randomNumber;
                	
                	System.out.println("brnumbe ="+brnNumber);
                }
            	
            	
            	
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Job 2: Runs every 30 minutes starting from 12:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job2() {
        executeJob(2);
    }

    // Job 3: Runs every 30 minutes starting from 1:00 AM
    @Scheduled(fixedDelay = DELAY)
    public void job3() {
        executeJob(3);
    }

    // Job 4: Runs every 30 minutes starting from 1:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job4() {
        executeJob(4);
    }

    // Job 5: Runs every 30 minutes starting from 2:00 AM
    @Scheduled(fixedDelay = DELAY)
    public void job5() {
        executeJob(5);
    }

    // Job 6: Runs every 30 minutes starting from 2:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job6() {
        executeJob(6);
    }

    // Job 7: Runs every 30 minutes starting from 3:00 AM
    @Scheduled(fixedDelay = DELAY)
    public void job7() {
        executeJob(7);
    }

    // Job 8: Runs every 30 minutes starting from 3:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job8() {
        executeJob(8);
    }

    // Job 9: Runs every 30 minutes starting from 4:00 AM
    @Scheduled(fixedDelay = DELAY)
    public void job9() {
        executeJob(9);
    }

    // Job 10: Runs every 30 minutes starting from 4:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job10() {
        executeJob(10);
    }

    // Job 11: Runs every 30 minutes starting from 5:00 AM
    @Scheduled(fixedDelay = DELAY)
    public void job11() {
        executeJob(11);
    }

    // Job 12: Runs every 30 minutes starting from 5:30 AM
    @Scheduled(fixedDelay = DELAY)
    public void job12() {
        executeJob(12);
    }
}


