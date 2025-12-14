package com.scheduler.db;

import java.time.LocalDateTime;

public class TaskConfig {
    private int taskId;
    private String taskName; // The field is defined here
    private String className;
    private String scheduleExpression;
    private LocalDateTime lastExecutedTime;
    private boolean isActive;

    // --- Constructor ---
    public TaskConfig(int taskId, String taskName, String className, String scheduleExpression, LocalDateTime lastExecutedTime, boolean isActive) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.className = className;
        this.scheduleExpression = scheduleExpression;
        this.lastExecutedTime = lastExecutedTime;
        this.isActive = isActive;
    }

    // --- Getters (The one you need is highlighted) ---
    public int getTaskId() { return taskId; }
    
    // <<< ADD THIS METHOD >>>
    public String getTaskName() { return taskName; }
    
    public String getClassName() { return className; }
    public String getScheduleExpression() { return scheduleExpression; }
    public LocalDateTime getLastExecutedTime() { return lastExecutedTime; }
    public boolean isActive() { return isActive; }
    
    // Setter needed for the scheduler to update the time
    public void setLastExecutedTime(LocalDateTime lastExecutedTime) {
        this.lastExecutedTime = lastExecutedTime;
    }
}