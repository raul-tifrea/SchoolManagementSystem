-- ============================================================
--  Faculty Catalog — Database Schema
--  Run this in MySQL Workbench or the MySQL CLI
-- ============================================================

DROP DATABASE IF EXISTS schooldb;
CREATE DATABASE schooldb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE schooldb;

-- -------------------------------------------------------
-- Users (authentication for all roles)
-- -------------------------------------------------------
CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role     ENUM('admin','teacher','student') NOT NULL
);

-- -------------------------------------------------------
-- Teachers
-- -------------------------------------------------------
CREATE TABLE teachers (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Students — fixed year (1-4) and group (1-5)
-- -------------------------------------------------------
CREATE TABLE students (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT UNIQUE NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100),
    study_year  INT NOT NULL DEFAULT 9,   -- 9 to 12
    study_group VARCHAR(1) NOT NULL DEFAULT 'A',   -- A to C
    CONSTRAINT chk_year  CHECK (study_year  BETWEEN 9 AND 12),
    CONSTRAINT chk_group CHECK (study_group IN ('A','B','C')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Subjects — one per teacher per year (business rule enforced by UNIQUE)
-- -------------------------------------------------------
CREATE TABLE subjects (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    teacher_id INT NOT NULL,
    study_year INT NOT NULL,              -- 9 to 12, which year this subject belongs to
    CONSTRAINT chk_subject_year CHECK (study_year BETWEEN 9 AND 12),
    UNIQUE KEY uq_teacher_year (teacher_id, study_year),   -- one subject per teacher per year
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
);

-- -------------------------------------------------------
-- Enrollments — admin explicitly links students to subjects
-- -------------------------------------------------------
CREATE TABLE enrollments (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    UNIQUE KEY uq_enrollment (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)  ON DELETE CASCADE
);

-- -------------------------------------------------------
-- Grades — multiple grades per student per subject (tests, exams, etc.)
-- -------------------------------------------------------
CREATE TABLE grades (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    grade      DECIMAL(4,2) NOT NULL,
    grade_date DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT chk_grade CHECK (grade BETWEEN 1 AND 10),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- -------------------------------------------------------
-- Absences — with motivated (excused) flag
-- -------------------------------------------------------
CREATE TABLE absences (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    subject_id   INT NOT NULL,
    absence_date DATE NOT NULL,
    motivated    BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- -------------------------------------------------------
-- Bootstrap: default admin account
-- -------------------------------------------------------
INSERT INTO users (username, password, role) VALUES ('admin', '$2a$12$9Q3Ql7iR8yv1c2KhVYJOB.kNh0USJlFJq5Ul9FS7hVFiH5ZqP8QC6', 'admin');

-- -------------------------------------------------------
-- Verification queries (run these to confirm setup)
-- -------------------------------------------------------
-- SHOW TABLES;
-- SELECT * FROM users;
