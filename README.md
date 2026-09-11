# School Catalog

A desktop application for managing a high school catalog. Built with Java and JavaFX, it supports three user roles — admin, teacher, and student — each with their own dashboard.

## What it does

**Admin** can add, edit, and delete users, assign subjects to teachers, and manage student enrollments.

**Teachers** can record grades and absences for students enrolled in their subjects, and mark absences as motivated.

**Students** can view their grades, GPA, and absences per subject.

All passwords are hashed with BCrypt. Every user can change their own password from within the app.

## Tech stack

- Java 16, JavaFX 17
- MySQL
- Maven
- jBCrypt for password hashing

## Requirements

- JDK 16 or later
- Maven
- MySQL Server running locally

## Database setup

Run `schema.sql` on your MySQL server. It creates the `schooldb` database and seeds a default admin account.

The connection settings are in `src/connection/ConnectionFactory.java`:
- URL: `jdbc:mysql://localhost:3306/schooldb`
- User: `root`
- Password: `0000`

Default admin login: username `admin`, password `0000`.

## Running the app

run `start.Starter` directly from your IDE.

## Project structure

```
src/
  connection/      Database connection factory
  dataaccess/      DAO classes for all database operations
  model/           Data models (User, Student, Teacher, Subject, Grade, Absence)
  presentation/    JavaFX views and controllers for each role
  start/           Application entry points
  util/            Password hashing utility
  css/             Application stylesheet
```
