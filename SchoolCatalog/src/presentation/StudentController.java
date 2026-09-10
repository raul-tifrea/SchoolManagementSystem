package presentation;

import dataaccess.AbsenceDAO;
import dataaccess.GradeDAO;
import dataaccess.SubjectDAO;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Absence;
import model.Grade;
import model.Subject;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import util.PasswordUtil;

public class StudentController {
    private Stage stage;
    private StudentView view;
    private GradeDAO gradeDAO;
    private SubjectDAO subjectDAO;
    private AbsenceDAO absenceDAO;
    private int studentUserId;

    public StudentController(StudentView view, Stage stage, int studentUserId) throws SQLException {
        this.stage = stage;
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.subjectDAO = new SubjectDAO();
        this.absenceDAO = new AbsenceDAO();
        this.studentUserId = studentUserId;

        stage.setTitle("School Catalog - Student Dashboard");
        stage.setScene(view.getScene());
        stage.sizeToScene();
        stage.centerOnScreen();

        initListeners();
        loadSubjects();
    }

    private void initListeners() {
        view.getLogoutButton().setOnAction(e -> logout());
        view.getChangePasswordButton().setOnAction(e -> changePassword());
        view.getSubjectBox().setOnAction(e -> loadGrades());
        view.getAbsenceSubjectBox().setOnAction(e -> loadAbsences());
    }

    private void logout() {
        new LoginController(stage);
    }

    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectDAO.getSubjectsForStudent(studentUserId);
            view.getSubjectBox().setItems(FXCollections.observableArrayList(subjects));
            view.getAbsenceSubjectBox().setItems(FXCollections.observableArrayList(subjects));
            
            if (!subjects.isEmpty()) {
                view.getSubjectBox().getSelectionModel().selectFirst();
                view.getAbsenceSubjectBox().getSelectionModel().selectFirst();
                loadGrades();
                loadAbsences();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGrades() {
        Subject subject = view.getSubjectBox().getValue();
        if (subject == null) return;
        
        try {
            List<Grade> grades = gradeDAO.getGradesForStudent(studentUserId, subject.getId());
            view.getGradesTable().setItems(FXCollections.observableArrayList(grades));
            
            double sum = 0;
            for (Grade g : grades) sum += g.getValue();
            
            if (!grades.isEmpty()) {
                double avg = sum / grades.size();
                view.getGpaLabel().setText(String.format("GPA: %.2f", avg));
                

                view.getGpaBanner().getStyleClass().removeAll("gpa-high", "gpa-medium", "gpa-low");
                if (avg >= 7.0) {
                    view.getGpaBanner().getStyleClass().add("gpa-high");
                } else if (avg >= 5.0) {
                    view.getGpaBanner().getStyleClass().add("gpa-medium");
                } else {
                    view.getGpaBanner().getStyleClass().add("gpa-low");
                }
            } else {
                view.getGpaLabel().setText("GPA: —");
                view.getGpaBanner().getStyleClass().removeAll("gpa-high", "gpa-medium", "gpa-low");
                view.getGpaBanner().getStyleClass().add("gpa-medium");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading grades.");
        }
    }

    private void loadAbsences() {
        Subject subject = view.getAbsenceSubjectBox().getValue();
        if (subject == null) return;
        
        try {
            List<Absence> absences = absenceDAO.getAbsencesForStudent(studentUserId, subject.getId());
            view.getAbsencesTable().setItems(FXCollections.observableArrayList(absences));
            
            long motivated = absences.stream().filter(Absence::isMotivated).count();
            long unexcused = absences.size() - motivated;
            view.getAbsenceSummaryLabel().setText(
                "Total: " + absences.size() + "  |  Motivated: " + motivated + "  |  Unexcused: " + unexcused
            );
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading absences.");
        }
    }

    private void changePassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));

        javafx.scene.control.PasswordField currentField = new javafx.scene.control.PasswordField();
        javafx.scene.control.PasswordField newField = new javafx.scene.control.PasswordField();
        javafx.scene.control.PasswordField confirmField = new javafx.scene.control.PasswordField();

        grid.add(new javafx.scene.control.Label("Current Password:"), 0, 0); grid.add(currentField, 1, 0);
        grid.add(new javafx.scene.control.Label("New Password:"), 0, 1); grid.add(newField, 1, 1);
        grid.add(new javafx.scene.control.Label("Confirm Password:"), 0, 2); grid.add(confirmField, 1, 2);
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
                dataaccess.AdminDAO dao = new dataaccess.AdminDAO();
                model.User u = dao.getUserById(studentUserId);
                if (u == null || !PasswordUtil.verify(current, u.getPassword())) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect.");
                    return;
                }
                dao.updatePassword(studentUserId, PasswordUtil.hash(newPass));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + ex.getMessage());
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