# LIBMAN - A Library Management System

This repository contains the code submitted for Software Engineering module (BS2202). The program provides an inventory management system within the context of a library, allowing for complete inventory management and circulation control using JavaFX and MySQL.

*All code for this project was formatted using clang.*

## Screenshots

View and Loan out Resources
<br/>

![243776329-5bbd5d95-c031-4cb0-8056-28f404bd926a](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/88cb2074-ff88-4c69-83c7-9454155ace94)


Register and Authenticate Users
<br/>

![243776534-45ea52ce-8a0f-4fc3-9634-96d696582409](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/164e10a1-2fdf-42f7-8624-a95b94e172d5)

Create, Delete and Update Resources
<br/>

![243776694-d5e38f23-985d-4189-b9f7-7e79cb8a6896](https://github.com/Tristan-Day/UoW-Library-Manager/assets/41393868/e85c06c0-6d57-435e-8061-35599c569efb)

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


