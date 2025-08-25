package presentation;

import dataaccess.GradeDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.List;
import java.awt.*;

public class TeacherController {
    private final TeacherView view;
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final SubjectDAO subjectDAO;
    private final int teacherId;

    public TeacherController(TeacherView view, int teacherId) throws SQLException {
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.teacherId = teacherId;

        initListeners();
        loadStudents();
        refreshTable();

    }


    public void initListeners(){
        view.getAddGradeButton().addActionListener(e -> addGrade());
        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = view.getTable().rowAtPoint(evt.getPoint());
                int col = view.getTable().columnAtPoint(evt.getPoint());
                if(row != -1 && col == 1){
                    GradeClick(row);
                }
            }
        });
    }

    public void GradeClick(int row){
        String studentName = (String) view.getTable().getValueAt(row, 0);
        String grade = (String) view.getTable().getValueAt(row, 1);
        String[] gradeArray = grade.split("\\s*,\\s*");
        String selectedGrade = (String) JOptionPane.showInputDialog(null, "Select grade to delete", "Delete Grade", JOptionPane.PLAIN_MESSAGE, null, gradeArray, gradeArray[0]);
        if(selectedGrade != null){
            int i = java.util.Arrays.asList(gradeArray).indexOf(selectedGrade);
            if(i>=0){
                int gradeId = view.getGradeIdMap().get(studentName).get(i);
                try{
                    if(gradeDAO.deleteGradeById(gradeId)){
                        refreshTable();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void addGrade(){
        try{
            Student student = view.getSelectedStudent();
            double grade = view.getGrade();
            if(gradeDAO.insertGrade(teacherId,student.getId(),grade)){
                refreshTable();
                view.clearForm();
            }
        } catch (SQLException e) {
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


    private void refreshTable() throws SQLException {
        try{
            view.updateTable(gradeDAO.getGradesForTeacher(teacherId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
