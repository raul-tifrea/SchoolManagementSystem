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
    private final GradeDAO gradeDAO;
    private final SubjectDAO subjectDAO;
    private final AbsenceDAO absenceDAO;
    private final int studentId;

    public StudentController(StudentView view, int studentId) throws SQLException {
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
        this.studentId = studentId;
        this.absenceDAO = new AbsenceDAO();

        initListeners();
        loadSubjects();
        refreshAbsences();
    }


    private void loadSubjects(){
        try{
            List<Subject> subjects = subjectDAO.getSubjectsForStudent(studentId);
            view.setSubjectsGrade(subjects);
            view.setAbsenceSubjects(subjects);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initListeners(){
        view.getSubjectBox().addActionListener(e -> loadGrades());
        view.getAbsenceSubjectBox().addActionListener(e-> loadAbsences());
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

    private void loadAbsences(){

        Subject absence = view.getSelectedAbsenceSubject();
        if(absence == null){
            return;
        }
        try{
            List<Absence> absences = absenceDAO.getAbsencesForStudent(studentId,absence.getId());
            view.updateAbsences(absences);
        } catch (Exception e) {
           JOptionPane.showMessageDialog(view, "Error loading absences", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAbsences() throws SQLException {
        try{
            Subject subject = view.getSelectedAbsenceSubject();
            if(subject == null){
                return;
            }
            List<Absence> absences = absenceDAO.getAbsencesForStudent(studentId,subject.getId());
            view.updateAbsences(absences);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}