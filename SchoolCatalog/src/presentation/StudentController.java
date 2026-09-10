package presentation;

import dataaccess.AbsenceDAO;
import dataaccess.GradeDAO;
import dataaccess.SubjectDAO;
import model.Absence;
import model.Grade;
import model.Subject;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class StudentController {
    private final StudentView view;
    private final GradeDAO   gradeDAO;
    private final SubjectDAO subjectDAO;
    private final AbsenceDAO absenceDAO;
    private final int studentUserId;

    public StudentController(StudentView view, int studentUserId) throws SQLException {
        this.view          = view;
        this.gradeDAO      = new GradeDAO();
        this.subjectDAO    = new SubjectDAO();
        this.absenceDAO    = new AbsenceDAO();
        this.studentUserId = studentUserId;

        initListeners();
        loadSubjects();
    }

    private void initListeners() {
        view.getSubjectBox().addActionListener(e -> loadGrades());
        view.getAbsenceSubjectBox().addActionListener(e -> loadAbsences());
    }

    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectDAO.getSubjectsForStudent(studentUserId);
            view.setSubjectsGrade(subjects);
            view.setAbsenceSubjects(subjects);
            // Trigger initial load
            loadGrades();
            loadAbsences();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadGrades() {
        Subject subject = view.getSelectedSubject();
        if (subject == null) return;
        try {
            List<Grade> grades = gradeDAO.getGradesForStudent(studentUserId, subject.getId());
            view.updateGrades(grades);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error loading grades", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAbsences() {
        Subject subject = view.getSelectedAbsenceSubject();
        if (subject == null) return;
        try {
            List<Absence> absences = absenceDAO.getAbsencesForStudent(studentUserId, subject.getId());
            view.updateAbsences(absences);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error loading absences", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}