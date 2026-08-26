# School Catalog

This is a Java-based desktop application for managing a school catalog. It provides a graphical user interface (GUI) to handle various school-related entities and their data.

## Features

The system allows users to manage the following data models:
- Students
- Teachers
- Subjects
- Grades
- Absences
- Users (for system access)

## Technology Stack

- **Language:** Java
- **GUI Framework:** Java Swing
- **Build Tool:** Maven
- **Database:** MySQL

## Prerequisites

Before running the application, ensure you have the following installed on your system:
- Java Development Kit (JDK) 16 or later
- Maven
- MySQL Server

## Database Setup

1. Make sure your local MySQL server is running.
2. Create a database named `schooldb`.
3. The application connects to the database using the following default credentials (configured in `connection.ConnectionFactory`):
   - **URL:** jdbc:mysql://localhost:3306/schooldb
   - **User:** root
   - **Password:** 0000

## How to Build and Run

1. Open a terminal or command prompt in the project root directory (where the `pom.xml` file is located).
2. Build the project using Maven:
   ```bash
   mvn clean package
   ```
3. Run the application:
   You can run the generated executable JAR file located in the `target` directory, or execute the main class directly from your IDE. The main entry point for the application is `start.Main`.

## Project Structure

- `src/model/`: Contains the data models for the application (Student, Teacher, Grade, etc.).
- `src/dataaccess/`: Contains the classes responsible for database interactions.
- `src/presentation/`: Contains the Swing GUI components (like `MainWindow`).
- `src/connection/`: Contains the `ConnectionFactory` for establishing the database connection.
- `src/start/`: Contains the `Main` class to launch the application.
