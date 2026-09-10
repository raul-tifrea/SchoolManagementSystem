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
                
                // Color code the GPA banner
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}