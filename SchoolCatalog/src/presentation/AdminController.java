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
        refreshUsersTable();
        refreshEnrollmentCombos();
        refreshSubjectsTable();
    }

    private void initListeners() {
        view.getAddButton().setOnAction(e -> addUser());
        view.getDeleteButton().setOnAction(e -> {
            try { deleteUser(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getSetSubjectTeacherButton().setOnAction(e -> {
            try { assignSubject(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
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
        view.getEditButton().setOnAction(e -> {
            try { editUser(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getChangePasswordButton().setOnAction(e -> changePassword(adminUserId));
        view.getLogoutButton().setOnAction(e -> logout());
        view.getDeleteSubjectButton().setOnAction(e -> {
            try { deleteSubject(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
        view.getReassignSubjectButton().setOnAction(e -> {
            try { reassignSubject(); } catch (SQLException ex) { ex.printStackTrace(); }
        });
    }

    private void logout() {
        new LoginController(stage);
    }

    private void addUser() {
        String username = view.getUsername();
        String password = view.getPassword();
        String name = view.getName();
        String email = view.getEmail();
        String role = view.getRole();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all required fields.");
            return;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email address.");
            return;
        }

        Integer studyYear = null;
        String studyGroup = null;
        if ("student".equalsIgnoreCase(role)) {
            studyYear = view.getYear();
            studyGroup = view.getGroup();
        }

        try {
            User user = new User(username, password, role);
            dao.insertUser(user, name, email, studyYear, studyGroup);
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            view.clearForm();
            refreshUsersTable();
            refreshEnrollmentCombos();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding user: " + ex.getMessage());
        }
    }

    private void deleteUser() throws SQLException {
        User selected = view.getUsersTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete user '" + selected.getUsername() + "'?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (dao.deleteUser(selected.getId())) {
                refreshUsersTable();
                refreshEnrollmentCombos();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
            }
        }
    }

    private void assignSubject() throws SQLException {
        User selected = view.getUsersTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a teacher.");
            return;
        }
        
        if (!"teacher".equalsIgnoreCase(selected.getRole())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selected user is not a teacher.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Assign Subject");
        dialog.setHeaderText("Assign a subject to " + selected.getUsername());

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField subjectNameField = new TextField();
        subjectNameField.setPromptText("Subject Name");
        Spinner<Integer> yearSpinner = new Spinner<>(9, 12, 9);

        grid.add(new Label("Subject Name:"), 0, 0);
        grid.add(subjectNameField, 1, 0);
        grid.add(new Label("Study Year (9-12):"), 0, 1);
        grid.add(yearSpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == assignButtonType) {
            String subjectName = subjectNameField.getText().trim();
            int studyYear = yearSpinner.getValue();
            if (subjectName.isEmpty()) return;

            if (dao.assignSubjectToTeacher(selected.getId(), subjectName, studyYear)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Subject assigned successfully.");
                refreshUsersTable();
                refreshEnrollmentCombos();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not assign subject.");
            }
        }
    }

    private void enrollStudent() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        Student student = view.getSelectedEnrollStudent();
        if (subject == null || student == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a subject and a student.");
            return;
        }
        dao.enrollStudent(student.getId(), subject.getId());
        refreshEnrollmentsTable();
    }

    private void enrollAll() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        if (subject == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a subject first.");
            return;
        }
        int count = dao.enrollAllStudentsInYear(subject.getId(), subject.getStudyYear());
        showAlert(Alert.AlertType.INFORMATION, "Success", count + " student(s) enrolled in " + subject + ".");
        refreshEnrollmentsTable();
    }

    private void removeEnrollment() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        Student student = view.getSelectedEnrollStudent();
        if (subject == null || student == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a subject and a student.");
            return;
        }
        dao.removeEnrollment(student.getId(), subject.getId());
        refreshEnrollmentsTable();
    }

    private void refreshUsersTable() throws SQLException {
        view.getUsersTable().setItems(FXCollections.observableArrayList(dao.getUsers()));
    }

    private void refreshEnrollmentCombos() throws SQLException {
        view.populateSubjectBox(subjectDAO.getAllSubjects());
        view.populateStudentBox(studentDAO.getAllStudents());
        refreshEnrollmentsTable();
    }

    private void refreshEnrollmentsTable() throws SQLException {
        Subject subject = view.getSelectedEnrollSubject();
        if (subject == null) {
            view.getEnrollmentsTable().setItems(FXCollections.observableArrayList());
            return;
        }
        List<Student> enrolled = studentDAO.getStudentsForSubject(subject.getId());
        List<String[]> rows = new ArrayList<>();
        for (Student s : enrolled) {
            rows.add(new String[]{
                s.getName() + " (" + s.getStudyYear() + s.getStudyGroup() + ")", 
                subject.toString()
            });
        }
        view.getEnrollmentsTable().setItems(FXCollections.observableArrayList(rows));
    }

    private void editUser() throws SQLException {
        User selected = view.getUsersTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a user to edit.");
            return;
        }
        if ("admin".equalsIgnoreCase(selected.getRole())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Admin accounts cannot be edited here.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Editing: " + selected.getUsername() + " (" + selected.getRole() + ")");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField usernameEditField = new TextField(selected.getUsername());
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        Spinner<Integer> yearSpinner = new Spinner<>(9, 12, 9);
        ComboBox<String> groupBox = new ComboBox<>();
        groupBox.getItems().addAll("A", "B", "C");
        groupBox.setValue("A");

        grid.add(new Label("Username:"), 0, 0); grid.add(usernameEditField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);

        if ("student".equalsIgnoreCase(selected.getRole())) {
            String[] details = dao.getStudentDetails(selected.getId());
            if (details != null) {
                nameField.setText(details[0]);
                emailField.setText(details[1]);
                yearSpinner.getValueFactory().setValue(Integer.parseInt(details[2]));
                groupBox.setValue(details[3]);
            }
            grid.add(new Label("Class (9-12):"), 0, 3); grid.add(yearSpinner, 1, 3);
            grid.add(new Label("Group (A-C):"), 0, 4); grid.add(groupBox, 1, 4);
        } else {
            String[] details = dao.getTeacherDetails(selected.getId());
            if (details != null) {
                nameField.setText(details[0]);
                emailField.setText(details[1]);
            }
        }

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveType) {
            String newUsername = usernameEditField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (newUsername.isEmpty() || name.isEmpty() || email.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username, name and email cannot be empty.");
                return;
            }
            try {
                if (!newUsername.equals(selected.getUsername())) {
                    dao.updateUsername(selected.getId(), newUsername);
                }
                boolean updated;
                if ("student".equalsIgnoreCase(selected.getRole())) {
                    updated = dao.updateStudentDetails(selected.getId(), name, email,
                            yearSpinner.getValue(), groupBox.getValue());
                } else {
                    updated = dao.updateTeacherDetails(selected.getId(), name, email);
                }
                if (updated) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
                    refreshUsersTable();
                    refreshEnrollmentCombos();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + ex.getMessage());
            }
        }
    }

    private void changePassword(int userId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        PasswordField currentField = new PasswordField();
        PasswordField newField = new PasswordField();
        PasswordField confirmField = new PasswordField();

        grid.add(new Label("Current Password:"), 0, 0); grid.add(currentField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1); grid.add(newField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2); grid.add(confirmField, 1, 2);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveType) {
            String current = currentField.getText();
            String newPass = newField.getText();
            String confirm = confirmField.getText();
            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
                return;
            }
            if (!newPass.equals(confirm)) {
                showAlert(Alert.AlertType.ERROR, "Error", "New passwords do not match.");
                return;
            }
            try {
                User u = dao.getUserById(userId);
                if (u == null || !util.PasswordUtil.verify(current, u.getPassword())) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect.");
                    return;
                }
                dao.updatePassword(userId, util.PasswordUtil.hash(newPass));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + ex.getMessage());
            }
        }
    }

    private void refreshSubjectsTable() throws SQLException {
        view.getSubjectsTable().setItems(
            FXCollections.observableArrayList(dao.getAllSubjectsDetailed()));
    }

    private void deleteSubject() throws SQLException {
        String[] selected = view.getSubjectsTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a subject to delete.");
            return;
        }
        int subjectId = Integer.parseInt(selected[0]);
        int[] stats = dao.getSubjectStats(subjectId);
        String warning = String.format(
            "Deleting '%s' will permanently remove:\n" +
            "  - %d enrollment(s)\n" +
            "  - %d grade(s)\n" +
            "  - %d absence(s)\n\n" +
            "This cannot be undone. Proceed?",
            selected[1], stats[0], stats[1], stats[2]);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Subject");
        confirm.setHeaderText(null);
        confirm.setContentText(warning);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dao.deleteSubject(subjectId);
            refreshSubjectsTable();
            refreshEnrollmentCombos();
            refreshUsersTable();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Subject deleted.");
        }
    }

    private void reassignSubject() throws SQLException {
        String[] selected = view.getSubjectsTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a subject to reassign.");
            return;
        }
        int subjectId = Integer.parseInt(selected[0]);

        List<User> teachers = dao.getUsers().stream()
            .filter(u -> "teacher".equalsIgnoreCase(u.getRole()))
            .collect(java.util.stream.Collectors.toList());
        if (teachers.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No teachers available.");
            return;
        }

        ChoiceDialog<User> dialog = new ChoiceDialog<>(teachers.get(0), teachers);
        dialog.setTitle("Reassign Teacher");
        dialog.setHeaderText("Reassign '" + selected[1] + "' (Year " + selected[2] + ") to a new teacher:");
        dialog.setContentText("Teacher:");

        Optional<User> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                dao.reassignSubjectTeacher(subjectId, result.get().getId());
                refreshSubjectsTable();
                refreshUsersTable();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Subject reassigned successfully.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
