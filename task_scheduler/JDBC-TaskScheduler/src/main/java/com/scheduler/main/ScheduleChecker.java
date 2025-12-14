package com.scheduler.main;

import com.scheduler.db.TaskConfig;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Handles the logic for checking if a task is currently due based on its configuration.
 */
public class ScheduleChecker {

    // Assuming a simple schedule format like "5 minutes" or "1 hour"
    public boolean isDue(TaskConfig config) {
        
        // 1. Get the last execution time
        LocalDateTime lastRun = config.getLastExecutedTime();
        
        // If a task has never run, it is immediately due.
        if (lastRun == null) {
            return true;
        }

        // 2. Parse the schedule expression (e.g., "5 minutes")
        String schedule = config.getScheduleExpression().trim().toLowerCase();
        
        try {
            String[] parts = schedule.split(" ");
            
            // We expect two parts: a number (duration) and a unit (minutes/hours)
            if (parts.length != 2) {
                // If the format is wrong, log a warning and skip the task (or throw an exception)
                System.err.println("WARNING: Invalid schedule format for Task ID " + config.getTaskId());
                return false; 
            }
            
            long duration = Long.parseLong(parts[0]);
            String unitString = parts[1];
            
            ChronoUnit unit;
            if (unitString.startsWith("minute")) {
                unit = ChronoUnit.MINUTES;
            } else if (unitString.startsWith("hour")) {
                unit = ChronoUnit.HOURS;
            } else if (unitString.startsWith("day")) {
                unit = ChronoUnit.DAYS;
            } else {
                System.err.println("WARNING: Unknown schedule unit for Task ID " + config.getTaskId() + ": " + unitString);
                return false;
            }
            
            // 3. Calculate the next scheduled execution time
            LocalDateTime nextRunTime = lastRun.plus(duration, unit);
            
            // 4. Compare with the current time (using java.time API)
            LocalDateTime currentTime = LocalDateTime.now();
            
            // The task is due if the current time is AT or AFTER the next scheduled time.
            return currentTime.isEqual(nextRunTime) || currentTime.isAfter(nextRunTime);
            
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Duration is not a valid number for Task ID " + config.getTaskId());
            return false;
        }
    }
}