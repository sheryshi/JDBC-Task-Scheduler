package com.scheduler.main;

import com.scheduler.db.TaskConfig;
import com.scheduler.db.TaskConfigDao;
import com.scheduler.tasks.ScheduledTask;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

class TaskSchedulerEngine {
    
    // DAO for database interactions
    private static final TaskConfigDao taskDao = new TaskConfigDao();
    
    // --- Helper class to run the task asynchronously and handle logging ---
    private static class TaskRunner implements Runnable {
        private final TaskConfig config;

        public TaskRunner(TaskConfig config) {
            this.config = config;
        }

        @Override
        public void run() {
            LocalDateTime startTime = LocalDateTime.now();
            String status = "FAILED";
            String errorMessage = null;

            try {
                // 1. Dynamically load and instantiate the task class
                // The Class.forName().getDeclaredConstructor().newInstance() is key to dynamic execution.
                Class<?> taskClass = Class.forName(config.getClassName());
                ScheduledTask task = (ScheduledTask) taskClass.getDeclaredConstructor().newInstance();
                
                // 2. Execute the task logic
                System.out.println("Executing Task: " + config.getClassName() + "...");
                task.execute(); // Calls the execute() method of your DailySystemOutTask
                
                // 3. Mark success and update last executed time in DB
                status = "SUCCESS";
                LocalDateTime endTime = LocalDateTime.now();
                taskDao.updateLastExecutedTime(config.getTaskId(), endTime);
                System.out.println("Task " + config.getClassName() + " completed successfully.");
                
                // 4. Log the result
                taskDao.logExecution(config.getTaskId(), startTime, endTime, status, errorMessage);

            } catch (SQLException e) {
                // Handle DAO/JDBC errors during logging/update
                System.err.println("DB Error during Task Execution/Logging for " + config.getClassName() + ": " + e.getMessage());
                errorMessage = "DB Error: " + e.getMessage();
            } catch (Exception e) {
                // Catch all other errors (ClassNotFound, InvocationTargetException, or errors from task.execute())
                System.err.println("Task Execution Failed for " + config.getClassName() + ": " + e.getMessage());
                errorMessage = e.toString();
            } finally {
                // Ensure logging of the failed status (if it failed before the success update)
                if ("FAILED".equals(status)) {
                    try {
                        taskDao.logExecution(config.getTaskId(), startTime, LocalDateTime.now(), status, errorMessage);
                    } catch (SQLException logEx) {
                        System.err.println("FATAL: Could not log execution failure: " + logEx.getMessage());
                    }
                }
            }
        }
    }

    // ... (Main loop will go here)
    
 // --- Main Scheduler Loop ---
    public static void main(String[] args) {
        System.out.println("JDBC-Configured Task Scheduler Started...");
        ScheduleChecker checker = new ScheduleChecker();
        
        // This is a simple, basic threading model. A real app might use an ExecutorService.
        
        while (true) {
            try {
                // 1. Poll the Database for active tasks
                List<TaskConfig> activeTasks = taskDao.getAllActiveTasks();
                
                for (TaskConfig config : activeTasks) {
                    
                    // 2. Check the schedule
                    if (checker.isDue(config)) {
                        System.out.println("Task DUE: " + config.getTaskName() + ". Submitting for execution.");
                        
                        // 3. Execute the task on a new thread (BASIC THREADING)
                        Thread taskThread = new Thread(new TaskRunner(config), "Task-" + config.getTaskId());
                        taskThread.start();
                    }
                }
                
                // 4. Wait for a short interval before polling again
                // This is the polling interval (e.g., check the DB every 15 seconds)
                Thread.sleep(15000); 
                
            } catch (SQLException e) {
                System.err.println("FATAL DB ERROR: Cannot read task configuration. Retrying in 60 seconds.");
                // Wait longer if the database is down
                try {
                    Thread.sleep(60000); 
                } catch (InterruptedException ignored) {}
            } catch (InterruptedException e) {
                System.out.println("Scheduler shut down initiated.");
                break;
            }
        }
    }
}
