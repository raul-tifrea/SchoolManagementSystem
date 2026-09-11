package presentation;

import dataaccess.AdminDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Student;
import model.Subject;
import model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminController {
    private Stage stage;
    private AdminView view;
    private AdminDAO dao;
    private SubjectDAO subjectDAO;
    private StudentDAO studentDAO;
    private int adminUserId;

    public AdminController(AdminView view, Stage stage, int adminUserId) throws SQLException {
        this.stage = stage;
        this.view = view;
        this.dao = new AdminDAO();
        this.subjectDAO = new SubjectDAO();
        this.studentDAO = new StudentDAO();
        this.adminUserId = adminUserId;

        stage.setTitle("School Catalog - Admin Dashboard");
        stage.setScene(view.getScene());
        stage.sizeToScene();
        stage.centerOnScreen();

        initListeners();
        refreshAll();
    }

    private void initListeners() {
        // Navigation
        view.getNavStudentsButton().setOnAction(e -> view.setContentPane(view.getStudentsView()));
        view.getNavTeachersButton().setOnAction(e -> view.setContentPane(view.getTeachersView()));
        view.getNavSubjectsButton().setOnAction(e -> view.setContentPane(view.getSubjectsView()));
        view.getNavEnrollmentsButton().setOnAction(e -> view.setContentPane(view.getEnrollmentsView()));

        // Students
        view.getAddStudentButton().setOnAction(e -> showAddStudentDialog());
        view.getEditStudentButton().setOnAction(e -> showEditStudentDialog());
        view.getDeleteStudentButton().setOnAction(e -> deleteUser(view.getStudentsTable(), "student"));

        // Teachers
        view.getAddTeacherButton().setOnAction(e -> showAddTeacherDialog());
        view.getEditTeacherButton().setOnAction(e -> showEditTeacherDialog());
        view.getDeleteTeacherButton().setOnAction(e -> deleteUser(view.getTeachersTable(), "teacher"));

        // Subjects
        view.getAddSubjectButton().setOnAction(e -> showAddSubjectDialog());
        view.getReassignSubjectButton().setOnAction(e -> {
            try { reassignSubject(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getDeleteSubjectButton().setOnAction(e -> {
            try { deleteSubject(); } catch (SQLException ex) { ex.printStackTrace(); }
        });

        // Enrollments
        view.getEnrollButton().setOnAction(e -> {
            try { enrollStudent(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getEnrollAllButton().setOnAction(e -> {
            try { enrollAll(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getRemoveEnrollButton().setOnAction(e -> {
            try { removeEnrollment(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getEnrollSubjectBox().setOnAction(e -> {
            try { refreshEnrollmentsTable(); } catch (SQLException ex) { ex.printStackTrace(); }
        });

        // General
        view.getChangePasswordButton().setOnAction(e -> changePassword(adminUserId));
        view.getLogoutButton().setOnAction(e -> logout());
    }

    private void refreshAll() throws SQLException {
        view.getStudentsTable().setItems(FXCollections.observableArrayList(dao.getAllStudentsDetailed()));
        view.getTeachersTable().setItems(FXCollections.observableArrayList(dao.getAllTeachersDetailed()));
        view.getSubjectsTable().setItems(FXCollections.observableArrayList(dao.getAllSubjectsDetailed()));
        view.populateSubjectBox(subjectDAO.getAllSubjects());
        view.populateStudentBox(studentDAO.getAllStudents());
        refreshEnrollmentsTable();
    }

    private void showAddStudentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Student");
        ButtonType saveType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        Spinner<Integer> yearSpinner = new Spinner<>(9, 12, 9);
        ComboBox<String> groupBox = new ComboBox<>(); groupBox.getItems().addAll("A", "B", "C"); groupBox.setValue("A");

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2); grid.add(nameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(emailField, 1, 3);
        grid.add(new Label("Year (9-12):"), 0, 4); grid.add(yearSpinner, 1, 4);
        grid.add(new Label("Group (A-C):"), 0, 5); grid.add(groupBox, 1, 5);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType) {
            try {
                User u = new User(userField.getText().trim(), passField.getText(), "student");
                dao.insertUser(u, nameField.getText().trim(), emailField.getText().trim(), yearSpinner.getValue(), groupBox.getValue());
                refreshAll();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void showAddTeacherDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Teacher");
        ButtonType saveType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField nameField = new TextField();
        TextField emailField = new TextField();

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2); grid.add(nameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(emailField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType) {
            try {
                User u = new User(userField.getText().trim(), passField.getText(), "teacher");
                dao.insertUser(u, nameField.getText().trim(), emailField.getText().trim(), null, null);
                refreshAll();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void showEditStudentDialog() {
        String[] selected = view.getStudentsTable().getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.ERROR, "Error", "Select a student."); return; }
        int userId = Integer.parseInt(selected[0]);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField userField = new TextField(selected[1]);
        TextField nameField = new TextField(selected[2]);
        TextField emailField = new TextField(selected[3]);
        Spinner<Integer> yearSpinner = new Spinner<>(9, 12, Integer.parseInt(selected[4]));
        ComboBox<String> groupBox = new ComboBox<>(); groupBox.getItems().addAll("A", "B", "C"); groupBox.setValue(selected[5]);

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Year:"), 0, 3); grid.add(yearSpinner, 1, 3);
        grid.add(new Label("Group:"), 0, 4); grid.add(groupBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType) {
            try {
                if (!userField.getText().equals(selected[1])) dao.updateUsername(userId, userField.getText().trim());
                dao.updateStudentDetails(userId, nameField.getText().trim(), emailField.getText().trim(), yearSpinner.getValue(), groupBox.getValue());
                refreshAll();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void showEditTeacherDialog() {
        String[] selected = view.getTeachersTable().getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.ERROR, "Error", "Select a teacher."); return; }
        int userId = Integer.parseInt(selected[0]);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Teacher");
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField userField = new TextField(selected[1]);
        TextField nameField = new TextField(selected[2]);
        TextField emailField = new TextField(selected[3]);

        grid.add(new Label("Username:"), 0, 0); grid.add(userField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType) {
            try {
                if (!userField.getText().equals(selected[1])) dao.updateUsername(userId, userField.getText().trim());
                dao.updateTeacherDetails(userId, nameField.getText().trim(), emailField.getText().trim());
                refreshAll();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void deleteUser(TableView<String[]> table, String type) {
        String[] selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.ERROR, "Error", "Select a " + type + " to delete."); return; }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete " + type + " '" + selected[1] + "'?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dao.deleteUser(Integer.parseInt(selected[0]));
                refreshAll();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void showAddSubjectDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Subject");
        ButtonType saveType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        Spinner<Integer> yearSpinner = new Spinner<>(9, 12, 9);
        ComboBox<User> teacherBox = new ComboBox<>();

        try {
            List<User> teachers = dao.getUsers().stream().filter(u -> "teacher".equalsIgnoreCase(u.getRole())).collect(Collectors.toList());
            teacherBox.getItems().setAll(teachers);
            if (!teachers.isEmpty()) teacherBox.setValue(teachers.get(0));
        } catch (Exception ignored) {}

        grid.add(new Label("Subject Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Year (9-12):"), 0, 1); grid.add(yearSpinner, 1, 1);
        grid.add(new Label("Teacher:"), 0, 2); grid.add(teacherBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType && teacherBox.getValue() != null) {
            try {
                dao.assignSubjectToTeacher(teacherBox.getValue().getId(), nameField.getText().trim(), yearSpinner.getValue());
                refreshAll();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void deleteSubject() throws SQLException {
        String[] selected = view.getSubjectsTable().getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.ERROR, "Error", "Select a subject to delete."); return; }
        
        int subjectId = Integer.parseInt(selected[0]);
        int[] stats = dao.getSubjectStats(subjectId);
        String warning = String.format("Deleting '%s' will permanently remove:\n  - %d enrollment(s)\n  - %d grade(s)\n  - %d absence(s)\n\nProceed?",
            selected[1], stats[0], stats[1], stats[2]);
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText(warning);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dao.deleteSubject(subjectId);
            refreshAll();
        }
    }

    private void reassignSubject() throws SQLException {
        String[] selected = view.getSubjectsTable().getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.ERROR, "Error", "Select a subject."); return; }
        
        List<User> teachers = dao.getUsers().stream().filter(u -> "teacher".equalsIgnoreCase(u.getRole())).collect(Collectors.toList());
        if (teachers.isEmpty()) return;

        ChoiceDialog<User> dialog = new ChoiceDialog<>(teachers.get(0), teachers);
        dialog.setTitle("Reassign Teacher");
        dialog.setHeaderText("Reassign '" + selected[1] + "' (Year " + selected[2] + ")");
        
        Optional<User> res = dialog.showAndWait();
        if (res.isPresent()) {
            dao.reassignSubjectTeacher(Integer.parseInt(selected[0]), res.get().getId());
            refreshAll();
        }
    }

    private void enrollStudent() throws SQLException {
        Subject sub = view.getSelectedEnrollSubject();
        Student stu = view.getSelectedEnrollStudent();
        if (sub != null && stu != null) { dao.enrollStudent(stu.getId(), sub.getId()); refreshAll(); }
    }

    private void enrollAll() throws SQLException {
        Subject sub = view.getSelectedEnrollSubject();
        if (sub != null) { dao.enrollAllStudentsInYear(sub.getId(), sub.getStudyYear()); refreshAll(); }
    }

    private void removeEnrollment() throws SQLException {
        Subject sub = view.getSelectedEnrollSubject();
        Student stu = view.getSelectedEnrollStudent();
        if (sub != null && stu != null) { dao.removeEnrollment(stu.getId(), sub.getId()); refreshAll(); }
    }

    private void refreshEnrollmentsTable() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        if (subject == null) { view.getEnrollmentsTable().setItems(FXCollections.observableArrayList()); return; }
        List<String[]> rows = studentDAO.getStudentsForSubject(subject.getId()).stream()
            .map(s -> new String[]{s.getName() + " (" + s.getStudyYear() + s.getStudyGroup() + ")", subject.toString()})
            .collect(Collectors.toList());
        view.getEnrollmentsTable().setItems(FXCollections.observableArrayList(rows));
    }

    private void changePassword(int userId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        PasswordField curField = new PasswordField(), newField = new PasswordField(), confField = new PasswordField();
        grid.add(new Label("Current:"), 0, 0); grid.add(curField, 1, 0);
        grid.add(new Label("New:"), 0, 1); grid.add(newField, 1, 1);
        grid.add(new Label("Confirm:"), 0, 2); grid.add(confField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == saveType) {
            try {
                User u = dao.getUserById(userId);
                if (u != null && util.PasswordUtil.verify(curField.getText(), u.getPassword()) && newField.getText().equals(confField.getText())) {
                    dao.updatePassword(userId, util.PasswordUtil.hash(newField.getText()));
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid current password or mismatch.");
                }
            } catch (SQLException ex) { showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage()); }
        }
    }

    private void logout() { new LoginController(stage); }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
}
