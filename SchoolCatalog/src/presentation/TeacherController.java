package presentation;

import dataaccess.AbsenceDAO;
import dataaccess.GradeDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import model.Absence;
import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class TeacherController {
    private final TeacherView view;
    private final GradeDAO   gradeDAO;
    private final StudentDAO studentDAO;
    private final SubjectDAO subjectDAO;
    private final AbsenceDAO absenceDAO;
    private final int teacherUserId;

    // Cached absences for the current view (to get ID by row index safely)
    private List<Absence> currentAbsences;

    public TeacherController(TeacherView view, int teacherUserId) throws SQLException {
        this.view          = view;
        this.gradeDAO      = new GradeDAO();
        this.studentDAO    = new StudentDAO();
        this.subjectDAO    = new SubjectDAO();
        this.absenceDAO    = new AbsenceDAO();
        this.teacherUserId = teacherUserId;

        initListeners();
        loadSubjects();
    }

    private void initListeners() {
        // Subject change → reload everything
        view.getSubjectCombo().addActionListener(e -> onSubjectChanged());

        // Grades tab
        view.getAddGradeButton().addActionListener(e -> addGrade());
        view.getGradesTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = view.getGradesTable().rowAtPoint(evt.getPoint());
                int col = view.getGradesTable().columnAtPoint(evt.getPoint());
                if (row != -1 && col == 2) gradeClick(row); // "Grades" column
            }
        });

        // Absences tab
        view.getAddAbsenceButton().addActionListener(e -> addAbsence());
        view.getDeleteAbsenceButton().addActionListener(e -> deleteAbsence());
        view.getMotivateAbsenceButton().addActionListener(e -> motivateAbsence());
        view.getAbsenceStudentCombo().addActionListener(e -> {
            try { refreshAbsences(); } catch (Exception ex) { throw new RuntimeException(ex); }
        });
    }

    // ── Subject loading ───────────────────────────────────────────────────────
    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectDAO.getSubjectsForTeacher(teacherUserId);
            view.setSubjectCombo(subjects);
            // Trigger initial load
            if (!subjects.isEmpty()) onSubjectChanged();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void onSubjectChanged() {
        Subject subject = view.getSelectedSubject();
        if (subject == null) return;
        try {
            List<Student> students = studentDAO.getStudentsForSubject(subject.getId());
            view.setStudentCombos(students);
            refreshGrades();
            refreshAbsences();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── Grades ────────────────────────────────────────────────────────────────
    private void addGrade() {
        Subject subject = view.getSelectedSubject();
        Student student = view.getSelectedStudent();
        if (subject == null || student == null) return;

        double grade;
        try {
            grade = view.getGrade();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Enter a valid number for grade.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (grade < 1 || grade > 10) {
            JOptionPane.showMessageDialog(view, "Grade must be between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (gradeDAO.insertGrade(student.getId(), subject.getId(), grade)) {
                refreshGrades();
                view.clearGradeForm();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void gradeClick(int row) {
        String studentName = (String) view.getGradesTable().getValueAt(row, 0);
        String gradesCell  = (String) view.getGradesTable().getValueAt(row, 2);
        String[] gradeArray = gradesCell.split("\\s*,\\s*");

        String selected = (String) JOptionPane.showInputDialog(
                null, "Select grade to delete", "Delete Grade",
                JOptionPane.PLAIN_MESSAGE, null, gradeArray, gradeArray[0]);

        if (selected != null) {
            int idx = java.util.Arrays.asList(gradeArray).indexOf(selected);
            if (idx >= 0) {
                List<Integer> ids = view.getGradeIdMap().get(studentName);
                if (ids != null && idx < ids.size()) {
                    try {
                        if (gradeDAO.deleteGradeById(ids.get(idx))) refreshGrades();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void refreshGrades() throws SQLException {
        Subject subject = view.getSelectedSubject();
        if (subject == null) return;
        view.updateGradesTable(gradeDAO.getGradesForTeacherSubject(subject.getId()));
    }

    // ── Absences ──────────────────────────────────────────────────────────────
    private void addAbsence() {
        Subject subject = view.getSelectedSubject();
        Student student = view.getSelectedAbsenceStudent();
        if (subject == null || student == null) return;

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        int result = JOptionPane.showConfirmDialog(view, dateChooser, "Select Absence Date", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION || dateChooser.getDate() == null) return;

        Date picked = dateChooser.getDate();
        LocalDate date = picked.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        try {
            absenceDAO.insertAbsence(student.getId(), subject.getId(), date);
            refreshAbsences();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteAbsence() {
        int row = view.getAbsencesTable().getSelectedRow();
        if (row == -1 || currentAbsences == null || row >= currentAbsences.size()) return;
        int absenceId = currentAbsences.get(row).getId();
        try {
            if (absenceDAO.deleteAbsenceById(absenceId)) refreshAbsences();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void motivateAbsence() {
        int row = view.getAbsencesTable().getSelectedRow();
        if (row == -1 || currentAbsences == null || row >= currentAbsences.size()) return;
        int absenceId = currentAbsences.get(row).getId();
        try {
            if (absenceDAO.motivateAbsence(absenceId)) refreshAbsences();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshAbsences() throws Exception {
        Subject subject = view.getSelectedSubject();
        Student student = view.getSelectedAbsenceStudent();
        if (subject == null) return;

        if (student != null) {
            currentAbsences = absenceDAO.getAbsencesForStudentInSubject(student.getId(), subject.getId());
        } else {
            currentAbsences = absenceDAO.getAbsencesForSubject(subject.getId());
        }
        view.updateAbsencesTable(currentAbsences);
    }
}