package com.scheduler.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskConfigDao {

    private static final String SELECT_ACTIVE_TASKS = 
        "SELECT task_id, task_name, class_name, schedule_expression, last_executed_time, is_active FROM task_config WHERE is_active = TRUE";
        
    private static final String INSERT_TASK_LOG = 
        "INSERT INTO task_log (task_id, start_time, end_time, status, error_message) VALUES (?, ?, ?, ?, ?)";
        
    private static final String UPDATE_LAST_EXECUTED = 
        "UPDATE task_config SET last_executed_time = ? WHERE task_id = ?";


    public List<TaskConfig> getAllActiveTasks() throws SQLException {
        List<TaskConfig> activeTasks = new ArrayList<>();
        
        // --- Try-with-Resources: Ensures Connection, Statement, and ResultSet are closed ---
        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACTIVE_TASKS);
             ResultSet rs = preparedStatement.executeQuery()) { 

            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String taskName = rs.getString("task_name");
                String className = rs.getString("class_name");
                String scheduleExpression = rs.getString("schedule_expression");
                boolean isActive = rs.getBoolean("is_active");
                
                // Handling DATETIME/TIMESTAMP conversion
                LocalDateTime lastExecutedTime = null;
                if (rs.getTimestamp("last_executed_time") != null) {
                    lastExecutedTime = rs.getTimestamp("last_executed_time").toLocalDateTime();
                }

                TaskConfig config = new TaskConfig(taskId, taskName, className, scheduleExpression, lastExecutedTime, isActive);
                activeTasks.add(config);
            }
        }
        return activeTasks;
    }
    
    // -------------------------------------------------------------------------------------
    //                         NEW METHOD: Log Execution to task_log
    // -------------------------------------------------------------------------------------
    
    /**
     * Inserts a record into the task_log table after a task finishes execution.
     */
    public void logExecution(int taskId, LocalDateTime startTime, LocalDateTime endTime, String status, String errorMessage) throws SQLException {
        // Log messages and error details will be managed by the calling Scheduler Engine
        
        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TASK_LOG)) {

            preparedStatement.setInt(1, taskId);
            // Converting Java LocalDateTime to JDBC Timestamp for storage
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(startTime));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(endTime));
            preparedStatement.setString(4, status);
            preparedStatement.setString(5, errorMessage);
            
            preparedStatement.executeUpdate();
        }
    }
    
    // -------------------------------------------------------------------------------------
    //                      NEW METHOD: Update last_executed_time
    // -------------------------------------------------------------------------------------
    
    /**
     * Updates the last_executed_time for a task after a successful run.
     */
    public void updateLastExecutedTime(int taskId, LocalDateTime executionTime) throws SQLException {
        
        try (Connection connection = DbConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LAST_EXECUTED)) {
            
            // Converting Java LocalDateTime to JDBC Timestamp for update
            preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(executionTime));
            preparedStatement.setInt(2, taskId);
            
            preparedStatement.executeUpdate();
        }
    }
}