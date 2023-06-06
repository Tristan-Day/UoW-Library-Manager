# LIBMAN - A Library Management System

This repository contains the code submitted for Software Engineering module (BS2202). The program provides an inventory management system within the context of a library, allowing for complete inventory management and circulation control using JavaFX and MySQL.

*All code for this project was formatted using clang.*

## Screenshots

View and Loan out Resources
<br/>

![Item View](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/0109e89f-7762-4b42-a2c6-facfc5841134)

Register and Authenticate Users
<br/>

![Registration Page](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/109861fc-d3f7-4d12-b0a0-d5b0ce2fd76d)

Create, Delete and Update Resources
<br/>

![Update Resources](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/2eeda1d3-4f46-41c1-8dc7-2f3a84e46798)

## System Requirements

To run this project, you will need to have the following tools and dependencies installed:

- Java 20.0.1 or later 
- MySQL Server 8.0.32 or later
- Maven 3.9.1 or later

## System Preparation

To ensure the operation of the program as intended, the SQL database dumps must be loaded into your MySQL server instance.

This can be accomplished using the provided **.cmd** script located under ```./database/``` and following the instructions below. 

*Note: If you wish to avoid logging in as the **root** user, please edit **load.cmd** with the relevant username before proceeding.*

1. Ensure the **mysql** executable is accessible through the system path.
2. From the directory ```./database/``` open **Windows Command Prompt**.
3. Run the load script by typing ```load.cmd```
4. Enter the account password for the root user.

When running the application, the system will prompt you to reconfigure the database connection settings.

## Running the Project

*Note: If you wish to run the provided tests, please do not delete any existing data prior to doing so as some is used for testing purposes*

1. Open the project from the root. (libman)
2. Ensure all libraries required and defined in the **pom.xml** file have been installed. *(Running **mvn compile** will do this)*
2. Run the **main** function located within ```./src/main/java/com/libman/App.java```

## User Authentication

After specifying valid database connection parameters you will be prompted to login.

The following credentials have been provided for the three account types:

### Librarian
```
Username: assessor-librarian
Password: Password12345
```

### Curator
```
Username: assessor-curator
Password: Password12345
```

### Administrator
```
Username: assessor-admin
Password: Password12345
```

## Running Tests

Tests are located under ```./src/test/java/com/Libman ```. and follow the standard **JUnit** testing structure, compatible with Visual Studio Code.

*Document generation outputs are sent to ```./exports```*


