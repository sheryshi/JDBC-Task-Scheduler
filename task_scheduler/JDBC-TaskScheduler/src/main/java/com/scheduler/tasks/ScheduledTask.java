package com.scheduler.tasks;

/**
 * The contract for all business logic tasks executed by the scheduler.
 * Any class configured in the task_config table must implement this.
 */
public interface ScheduledTask {

    /**
     * Executes the core logic of the scheduled job (e.g., reporting, data cleaning).
     * @throws Exception Allows the task to report failure back to the scheduler.
     */
//    void execute() throws Exception;
	void execute();
}
