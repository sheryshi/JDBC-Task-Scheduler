JDBC-Configured Task Scheduler 
  Project Description: 
	- Developed a backend scheduler to execute tasks based on dynamic configuration read directly from the database. 
	- Decoupled task schedules from the code, allowing updates without redeployment. - Used Core Java's java. time API for accurate time-based execution logic. 
  Technology Used:
	- Core Java 8+ (java. time API, Basic Threading) 
	- JDBC (PreparedStatement, Try-with-Resources) 
	- SQL (MySQL) 
  
  Role/Contribution: 
	- Modeled the task_config and task_log tables for configuration and auditing. 
	- Implemented JDBC methods for securely reading schedules (SELECT) and writing execution logs (INSERT). 
	- Ensured resource efficiency by applying Try-with-Resources to all JDBC objects
