package presentation;

import dataaccess.GradeDAO;
import dataaccess.SubjectDAO;
import model.Grade;
import model.Subject;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class StudentController {
    private final StudentView view;
    private final GradeDAO gradeDAO;
    private final SubjectDAO subjectDAO;
    private final int studentId;

    public StudentController(StudentView view, int studentId) throws SQLException {
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
        this.studentId = studentId;
        initListeners();
        loadSubjects();
    }


    private void loadSubjects(){
        try{
            List<Subject> subjects = subjectDAO.getSubjectsForStudent(studentId);
            view.setSubjects(subjects);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initListeners(){
        view.getSubjectBox().addActionListener(e -> loadGrades());
    }

    private void loadGrades(){
        Subject subject = view.getSelectedSubject();
        if(subject == null){
            return;
        }
        try{
            List<Grade> grades = gradeDAO.getGradesForStudent(studentId,subject.getId());
            view.updateGrades(grades);
        }catch (SQLException e){
            JOptionPane.showMessageDialog(view, "Error loading grades", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}