package presentation;

import dataaccess.AdminDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import model.Student;
import model.Subject;
import model.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    private final AdminView view;
    private final AdminDAO  dao;
    private final SubjectDAO subjectDAO;
    private final StudentDAO studentDAO;

    public AdminController(AdminView view) throws SQLException {
        this.view       = view;
        this.dao        = new AdminDAO();
        this.subjectDAO = new SubjectDAO();
        this.studentDAO = new StudentDAO();

        initListeners();
        refreshUsersTable();
        refreshEnrollmentCombos();
    }

    private void initListeners() {
        view.getAddButton().addActionListener(e -> addUser());
        view.getDeleteButton().addActionListener(e -> {
            try { deleteUser(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
        view.getSetSubjectTeacherButton().addActionListener(e -> {
            try { assignSubject(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
        view.getEnrollButton().addActionListener(e -> {
            try { enrollStudent(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
        view.getEnrollAllButton().addActionListener(e -> {
            try { enrollAll(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
        view.getRemoveEnrollButton().addActionListener(e -> {
            try { removeEnrollment(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
        // Refresh enrollment table when subject changes
        view.getEnrollSubjectBox().addActionListener(e -> {
            try { refreshEnrollmentsTable(); } catch (SQLException ex) { throw new RuntimeException(ex); }
        });
    }

    // ── Add user ──────────────────────────────────────────────────────────────
    private void addUser() {
        String username = view.getUsername();
        String password = view.getPassword();
        String name     = view.getName();
        String email    = view.getEmail();
        String role     = view.getRole();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(view, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer studyYear  = null;
        Integer studyGroup = null;
        if ("student".equalsIgnoreCase(role)) {
            studyYear  = view.getYear();
            studyGroup = view.getGroup();
        }

        try {
            User user = new User(username, password, role);
            dao.insertUser(user, name, email, studyYear, studyGroup);
            JOptionPane.showMessageDialog(view, "User added successfully.");
            view.clearForm();
            refreshUsersTable();
            refreshEnrollmentCombos();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Delete user ───────────────────────────────────────────────────────────
    private void deleteUser() throws SQLException {
        int userId = view.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(view, "Please select a user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (dao.deleteUser(userId)) {
            refreshUsersTable();
            refreshEnrollmentCombos();
        } else {
            JOptionPane.showMessageDialog(view, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Assign subject to teacher ─────────────────────────────────────────────
    private void assignSubject() throws SQLException {
        int userId = view.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(view, "Please select a teacher.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = dao.getUserById(userId);
        if (user == null || !"teacher".equalsIgnoreCase(user.getRole())) {
            JOptionPane.showMessageDialog(view, "Selected user is not a teacher.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField subjectNameField = new JTextField(12);
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        JPanel panel = new JPanel(new java.awt.GridLayout(2, 2, 6, 6));
        panel.add(new JLabel("Subject name:"));  panel.add(subjectNameField);
        panel.add(new JLabel("Study year (1-4):")); panel.add(yearSpinner);

        int result = JOptionPane.showConfirmDialog(view, panel, "Assign Subject", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String subjectName = subjectNameField.getText().trim();
        int studyYear = (int) yearSpinner.getValue();
        if (subjectName.isEmpty()) return;

        try {
            if (dao.assignSubjectToTeacher(userId, subjectName, studyYear)) {
                JOptionPane.showMessageDialog(view, "Subject assigned successfully.");
                refreshUsersTable();
                refreshEnrollmentCombos();
            } else {
                JOptionPane.showMessageDialog(view, "Could not assign subject (teacher may already have a subject for that year).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Enrollment ────────────────────────────────────────────────────────────
    private void enrollStudent() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        Student student = view.getSelectedEnrollStudent();
        if (subject == null || student == null) {
            JOptionPane.showMessageDialog(view, "Select a subject and a student.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dao.enrollStudent(student.getId(), subject.getId());
        refreshEnrollmentsTable();
    }

    private void enrollAll() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        if (subject == null) {
            JOptionPane.showMessageDialog(view, "Select a subject first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int count = dao.enrollAllStudentsInYear(subject.getId(), subject.getStudyYear());
        JOptionPane.showMessageDialog(view, count + " student(s) enrolled in " + subject + ".");
        refreshEnrollmentsTable();
    }

    private void removeEnrollment() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        Student student = view.getSelectedEnrollStudent();
        if (subject == null || student == null) return;
        dao.removeEnrollment(student.getId(), subject.getId());
        refreshEnrollmentsTable();
    }

    // ── Refresh helpers ───────────────────────────────────────────────────────
    private void refreshUsersTable() throws SQLException {
        view.updateUsersTable(dao.getUsers());
    }

    private void refreshEnrollmentCombos() throws SQLException {
        view.populateSubjectBox(subjectDAO.getAllSubjects());
        view.populateStudentBox(studentDAO.getAllStudents());
        refreshEnrollmentsTable();
    }

    private void refreshEnrollmentsTable() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        if (subject == null) return;
        List<Student> enrolled = studentDAO.getStudentsForSubject(subject.getId());
        List<String[]> rows = new ArrayList<>();
        for (Student s : enrolled) {
            rows.add(new String[]{s.getName() + " (Yr" + s.getStudyYear() + " Gr" + s.getStudyGroup() + ")", subject.toString()});
        }
        view.updateEnrollmentsTable(rows);
    }
}
