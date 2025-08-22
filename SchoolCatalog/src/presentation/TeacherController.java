package presentation;

import dataaccess.GradeDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class TeacherController {
    private final TeacherView view;
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final SubjectDAO subjectDAO;
    private final int teacherId;

    public TeacherController(TeacherView view, int teacherId) {
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.teacherId = teacherId;


    }


    public void initListeners(){
        view.getAddGradeButton().addActionListener(e -> addGrade());
        view.getDeleteGradeButton().addActionListener(e -> deleteGrade());
    }


    public void addGrade(){
        try{
            Student student = view.getSelectedStudent();
            Subject subject = view.getSelectedSubject();
            double grade;

            if(student == null || subject == null){
                JOptionPane.showMessageDialog(view, "Please select both a student and a subject", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try{
                grade = view.getGrade();

            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(view, "Please enter a valid grade", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(gradeDAO.insertGradeForTeacher(teacherId,student.getId(),subject.getId(),grade)){
                refreshTable();
                view.clearForm();
            }else{
                JOptionPane.showMessageDialog(view, "Failed to add grade", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error adding grade", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);

        }
    }

    private void deleteGrade(){
        try{
            int gradeId = view.getSelectedGradeID();
            if(gradeId == -1){
                JOptionPane.showMessageDialog(view, "Please select a grade", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(gradeDAO.deleteGradeForTeacher(teacherId,gradeId)){
                refreshTable();
            }else{
                JOptionPane.showMessageDialog(view, "Failed to delete grade", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error deleting grade", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    private void loadStudents(){
        try{
            List<Student> students = studentDAO.getStudentsForTeacher(teacherId);
            view.setStudentCombo(students);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSubjects(){
        try{
            List<Subject> subjects = subjectDAO.getSubjectsForTeacher(teacherId);
            view.setSubjectCombo(subjects);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshTable() throws SQLException {
        try{
            List<Grade> grades = gradeDAO.getGradesForTeacher(teacherId);
            view.updateTable(grades);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error refreshing table", "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }



}
