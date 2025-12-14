package com.scheduler.tasks;

import java.time.LocalDateTime;

public class DailySystemOutTask implements ScheduledTask {

    @Override
    public void execute() { // NOTICE: I removed 'throws Exception' here for a simpler fix
        // --- This is the business logic ---
        String threadName = Thread.currentThread().getName();
        
        System.out.println("----------------------------------------------");
        System.out.println("Task Executing: DailySystemOutTask");
        System.out.println("Time: " + LocalDateTime.now());
        System.out.println("Running on Thread: " + threadName);
        System.out.println("----------------------------------------------");
        
        // Simulating a short workload
        try {
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            // If the thread is interrupted while sleeping, 
            // log the error but allow the method to finish cleanly.
            System.err.println("WARNING: DailySystemOutTask was interrupted.");
            // Reset the interrupt status
            Thread.currentThread().interrupt(); 
        }
    }
}