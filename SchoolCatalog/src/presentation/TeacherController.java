package presentation;

import dataaccess.AbsenceDAO;
import dataaccess.GradeDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import model.Absence;
import model.Student;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
public class TeacherController {
    private final TeacherView view;
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final SubjectDAO subjectDAO;
    private final AbsenceDAO absenceDAO;
    private final int teacherId;

    public TeacherController(TeacherView view, int teacherId) throws SQLException {
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.teacherId = teacherId;
        this.absenceDAO = new AbsenceDAO();

        initListeners();
        loadStudents();
        refreshTable();

    }


    public void initListeners(){
        view.getAddGradeButton().addActionListener(e -> addGrade());
        view.getGradesTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = view.getGradesTable().rowAtPoint(evt.getPoint());
                int col = view.getGradesTable().columnAtPoint(evt.getPoint());
                if(row != -1 && col == 1){
                    GradeClick(row);
                }
            }
        });
        view.getAddAbsenceButton().addActionListener(e->addAbsence());
        view.getDeleteAbsenceButton().addActionListener(e->deleteAbsence());
    }

    public void GradeClick(int row){
        String studentName = (String) view.getGradesTable().getValueAt(row, 0);
        String grade = (String) view.getGradesTable().getValueAt(row, 1);
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
            if(grade < 1 || grade > 10)
            {
                JOptionPane.showMessageDialog(view, "Grade must be between 1 and 10", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
            view.setAbsenceStudentCombo(students);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void refreshTable() throws SQLException {
        try{
            view.updateGradesTable(gradeDAO.getGradesForTeacher(teacherId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addAbsence(){
        Student student = view.getSelectedAbsenceStudent();
        if(student == null){
            return;
        }
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        int result = JOptionPane.showConfirmDialog(view, dateChooser, "Select Absence Date", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION && dateChooser.getDate() != null){
            Date dateselected = dateChooser.getDate();
            LocalDate date = dateselected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            try {
                if (absenceDAO.insertAbsence(teacherId, student.getId(), date)){
                    refreshAbsences();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteAbsence(){
        int row = view.getAbsencesTable().getSelectedRow();
        if(row == -1){
            return;
        }
        try{
            List<Absence> absences = absenceDAO.getAbsencesForTeacher(teacherId);
            int id = absences.get(row).getId();
            if(absenceDAO.deleteAbsenceById(id)){
                refreshAbsences();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshAbsences() throws SQLException{
        try{
            view.updateAbsencesTable(absenceDAO.getAbsencesForTeacher(teacherId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}