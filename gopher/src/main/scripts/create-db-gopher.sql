# Gopher database

DROP DATABASE IF EXISTS gopher;
CREATE DATABASE gopher;

USE gopher;

# A suite. A suite has many test_suite.
CREATE TABLE suite (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The suite name
	name VARCHAR(80) NOT NULL,
	#The version
	version VARCHAR(80) NOT NULL,
	description VARCHAR(80),
	#Allow this case or not (y or n) defaults to yes
	runmode BOOLEAN,
	PRIMARY KEY (id)
);

#An instance of a suite, running or has run.
CREATE TABLE suite_instance (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#Foreign key into test_suite
	suite_id INT,
	#Start time for suite
	start_time TIMESTAMP NOT NULL,
	#End time for suite
	end_time TIMESTAMP NOT NULL,
	FOREIGN KEY(suite_id) REFERENCES suite(id),
	PRIMARY KEY (id)
);

# A test suite. A test_suite has many test_class.
CREATE TABLE test_suite (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The test suite name
	name VARCHAR(80) NOT NULL,
	#The version
	version VARCHAR(80) NOT NULL,
	description VARCHAR(80),
	#Allow this case or not (y or n) defaults to yes
	runmode BOOLEAN,
	PRIMARY KEY (id)
);

#An instance of a test_suite, running or has run.
CREATE TABLE test_suite_instance (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#Foreign key into test_suite
	test_suite_id INT,
	#Foreign key into suite_instance
	suite_instance_id INT,
	#Start time for test_suite
	start_time TIMESTAMP NOT NULL,
	#End time for test_suite
	end_time TIMESTAMP NOT NULL,
	FOREIGN KEY(test_suite_id) REFERENCES test_suite(id),
	FOREIGN KEY(suite_instance_id) REFERENCES suite_instance(id),
	PRIMARY KEY (id)
);

# A suite test suite. A many-to-many relation between suite and test_suite.
CREATE TABLE suite_test_suite (
	#Foreign key into suite
	suite_id INT,
	#Foreign key into test_suite
	test_suite_id INT,
	FOREIGN KEY(suite_id) REFERENCES suite(id),
	FOREIGN KEY(test_suite_id) REFERENCES test_suite(id)
);

# A test class.
CREATE TABLE test_class (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The test class name
	name VARCHAR(80) NOT NULL,
	#The version
	version VARCHAR(80) NOT NULL,
	#The fully qualified test class name
	class_name VARCHAR(200) NOT NULL,
	#A description of this test class
	description VARCHAR(300),
	#Allow this class to run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#The Jira issue id
	jira_issue VARCHAR(80) NOT NULL,
	#Install date/time
	install_time TIMESTAMP NOT NULL,
	PRIMARY KEY (id)
);

# A test_suite test class. A many-to-many relation between test_suite and test_class.
CREATE TABLE test_suite_test_class (
	#Foreign key into test suite
	test_suite_id INT,
	#Foreign key into test_class
	test_class_id INT,
	FOREIGN KEY(test_suite_id) REFERENCES test_suite(id),
	FOREIGN KEY(test_class_id) REFERENCES test_class(id)
);

# A definition of a test class argument
CREATE TABLE test_data_type (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#A name or label for the data value
	data_name VARCHAR(80) NOT NULL,
	#The type of data. See
	#enum com.echostar.gopher.persist.DataTypeEnum
	#for supported values.
	data_type VARCHAR(80) NOT NULL,
	#The data role - TBD.
	role VARCHAR(80) NOT NULL,
	PRIMARY KEY (id)
);

# A test class test data type. A many-to-many relation between test_class and test_data_type.
CREATE TABLE test_class_test_data_type (
	#Foreign key into test_class
	test_class_id INT,
	#Foreign key into test_data_type
	test_data_type_id INT,
	FOREIGN KEY(test_class_id) REFERENCES test_class(id),
	FOREIGN KEY(test_data_type_id) REFERENCES test_data_type(id)
);

# A test case. A test_case relates a test_class with a set of test_data.
CREATE TABLE test_case (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The test case name
	name VARCHAR(100) NOT NULL,
	#The version
	version VARCHAR(80) NOT NULL,
	#Allow this case to run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#Foreign key into test_class
	test_class_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY(test_class_id) REFERENCES test_class(id)
);

# A test case argument value. A test_data has a test_data_type.
CREATE TABLE test_data (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#Foreign key into test_data_type
	test_data_type_id INT,
	#The value for the argument
	data_value VARCHAR(20000) NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY(test_data_type_id) REFERENCES test_data_type(id)
);

# A page element locator
CREATE TABLE element_locator (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The name of the locator
	name VARCHAR(80) NOT NULL,
	#The locator value (id number, xpath string, classname string etc.)
	value VARCHAR(200) NOT NULL,
	#The locator type. See enum com.echostar.gopher.persist.LocatorTypeEnum for supported values.
	locator_type VARCHAR(80) NOT NULL,
	#A description of the locator
	description VARCHAR(300),
	PRIMARY KEY (id)
);

# Test case test data. A many-to-many relation between test_case and test_data.
CREATE TABLE test_case_test_data (
	#Foreign key into test_case
	test_case_id INT,
	#Foreign key into test_data
	test_data_id INT,
	FOREIGN KEY(test_case_id) REFERENCES test_case(id),
	FOREIGN KEY(test_data_id) REFERENCES test_data(id)
);

# Test case element locator. A many-to-many relation between test_case and element_locator.
CREATE TABLE test_case_element_locator (
	#Foreign key into test_case
	test_case_id INT,
	#Foreign key into element_locator
	element_locator_id INT,
	FOREIGN KEY(test_case_id) REFERENCES test_case(id),
	FOREIGN KEY(element_locator_id) REFERENCES element_locator(id)
);

# A test node
CREATE TABLE test_node (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The address of the Selenium node
	node_ip VARCHAR(80) NOT NULL,
	#The port number of the Selenium node
	node_port VARCHAR(80) NOT NULL,
	#The platform running the browser
	platform VARCHAR(80) NOT NULL,
	#The user account name
	user_name VARCHAR(100) NOT NULL,
	#The user account encrypted password
	user_password VARCHAR(200),
	#The directory for our files
	install_dir VARCHAR(80) NOT NULL,
	#The name of the Selenium server file
	selenium_server VARCHAR(80) NOT NULL,
	PRIMARY KEY (id)
);

# A suite decorator
CREATE TABLE suite_decorator (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The URL of web page to be tested
	url VARCHAR(80),
	#The name
	name VARCHAR(80),
	#The web browser
	browser VARCHAR(80),
	#Allow this run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#Foreign key into suite
	suite_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY(suite_id) REFERENCES suite(id)
);

# A test_suite run
CREATE TABLE test_suite_decorator (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The URL of web page to be tested
	url VARCHAR(80),
	#The web browser
	browser VARCHAR(80),
	#Allow this run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#Foreign key into suite
	suite_id INT,
	#Foreign key into test_suite
	test_suite_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY(suite_id) REFERENCES suite(id),
	FOREIGN KEY(test_suite_id) REFERENCES test_suite(id)
);

# A test class decorator
CREATE TABLE test_class_decorator (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The URL of web page to be tested
	url VARCHAR(80),
	#The web browser
	browser VARCHAR(80),
	#Allow this run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#Foreign key into test_suite
	test_suite_id INT,
	#Foreign key into test_class
	test_class_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY(test_suite_id) REFERENCES test_suite(id),
	FOREIGN KEY(test_class_id) REFERENCES test_class(id)
);

# A test run
CREATE TABLE test_run (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The URL of web page to be tested.
	#This is optional and may be defined on a decorator.
	#If defined here, it will take precedence over a decorator.
	url VARCHAR(80),
	#The web browser
	browser VARCHAR(80),
	#Allow this run or not (y or n) defaults to yes
	runmode BOOLEAN,
	#Foreign key into test_case
	test_case_id INT,
	#Foreign key into test_node
	test_node_id INT,
	PRIMARY KEY (id),
	FOREIGN KEY(test_case_id) REFERENCES test_case(id),
	FOREIGN KEY(test_node_id) REFERENCES test_node(id)
);

# A test run result.  A row is inserted here when a test run is finished.
CREATE TABLE test_run_result (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#Foreign key into test_run
	test_run_id INT,
	#Foreign key into suite_instance
	suite_instance_id INT,
	#Foreign key into test_suite_instance
	test_suite_instance_id INT,
	#Result: passed=1, failed=0
	result BOOLEAN NOT NULL,
	#Text describing failure
	failure_message VARCHAR(4000),
	#Start time for test case
	start_time TIMESTAMP NOT NULL,
	#End time for test case
	end_time TIMESTAMP NOT NULL,
	#User name for this result
	user VARCHAR(80),
	#The URL used for the test
	url VARCHAR(80),
	FOREIGN KEY(suite_instance_id) REFERENCES suite_instance(id),
	FOREIGN KEY(test_suite_instance_id) REFERENCES test_suite_instance(id),
	FOREIGN KEY(test_run_id) REFERENCES test_run(id),
	PRIMARY KEY (id)
);

# A browser
CREATE TABLE browser (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The name of the browser
	name VARCHAR(80) NOT NULL,
	#The browser type (see BrowserEnum.java)
	type VARCHAR(80) NOT NULL,
	PRIMARY KEY (id)
);

# A test node browser. A many-to-many relation between test_node and browser.
CREATE TABLE test_node_browser (
	#Foreign key into test_node
	test_node_id INT,
	#Foreign key into browser
	browser_id INT,
	FOREIGN KEY(test_node_id) REFERENCES test_node(id),
	FOREIGN KEY(browser_id) REFERENCES browser(id)
);

# A test exception.
CREATE TABLE test_exception (
	#The primary key
	id INT auto_increment NOT NULL UNIQUE,
	#The exception class
	exception_class VARCHAR(100) NOT NULL,
	#The exception message
	message VARCHAR(4000),
	#The stacktrace
	stacktrace VARCHAR(10000) NOT NULL,
	#Foreign key into test_run_result
	test_run_result_id INT,
	FOREIGN KEY(test_run_result_id) REFERENCES test_run_result(id),
	PRIMARY KEY (id)
);
