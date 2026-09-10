package presentation;

import dataaccess.AbsenceDAO;
import dataaccess.GradeDAO;
import dataaccess.StudentDAO;
import dataaccess.SubjectDAO;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Absence;
import model.Grade;
import model.Student;
import model.Subject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TeacherController {
    private Stage stage;
    private TeacherView view;
    private GradeDAO gradeDAO;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;
    private AbsenceDAO absenceDAO;
    private int teacherUserId;

    // We store this to map studentName -> list of grade IDs
    private Map<String, List<Integer>> gradeIdMap = new HashMap<>();

    public TeacherController(TeacherView view, Stage stage, int teacherUserId) throws SQLException {
        this.stage = stage;
        this.view = view;
        this.gradeDAO = new GradeDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.absenceDAO = new AbsenceDAO();
        this.teacherUserId = teacherUserId;

        stage.setTitle("School Catalog - Teacher Dashboard");
        stage.setScene(view.getScene());
        stage.sizeToScene();
        stage.centerOnScreen();

        initListeners();
        loadSubjects();
    }

    private void initListeners() {
        view.getSubjectCombo().setOnAction(e -> onSubjectChanged());
        view.getLogoutButton().setOnAction(e -> logout());

        view.getAddGradeButton().setOnAction(e -> addGrade());
        view.getGradesTable().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                gradeClick();
            }
        });

        view.getAbsenceStudentCombo().setOnAction(e -> {
            try { refreshAbsences(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        view.getAddAbsenceButton().setOnAction(e -> addAbsence());
        view.getDeleteAbsenceButton().setOnAction(e -> deleteAbsence());
        view.getMotivateAbsenceButton().setOnAction(e -> motivateAbsence());
    }

    private void logout() {
        new LoginController(stage);
    }

    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectDAO.getSubjectsForTeacher(teacherUserId);
            view.getSubjectCombo().setItems(FXCollections.observableArrayList(subjects));
            if (!subjects.isEmpty()) {
                view.getSubjectCombo().getSelectionModel().selectFirst();
                onSubjectChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onSubjectChanged() {
        Subject subject = view.getSubjectCombo().getValue();
        if (subject == null) return;
        try {
            List<Student> students = studentDAO.getStudentsForSubject(subject.getId());
            view.getStudentCombo().setItems(FXCollections.observableArrayList(students));
            view.getAbsenceStudentCombo().setItems(FXCollections.observableArrayList(students));
            refreshGrades();
            refreshAbsences();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshGrades() throws SQLException {
        Subject subject = view.getSubjectCombo().getValue();
        if (subject == null) return;

        List<Grade> grades = gradeDAO.getGradesForTeacherSubject(subject.getId());
        gradeIdMap.clear();

        Map<String, StringBuilder> gradesStr = new LinkedHashMap<>();
        Map<String, List<Double>> avgMap = new LinkedHashMap<>();

        for (Grade g : grades) {
            String name = g.getStudentName();
            gradesStr.computeIfAbsent(name, k -> new StringBuilder()).append(g.getValue()).append(", ");
            gradeIdMap.computeIfAbsent(name, k -> new ArrayList<>()).add(g.getId());
            avgMap.computeIfAbsent(name, k -> new ArrayList<>()).add(g.getValue());
        }

        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, StringBuilder> e : gradesStr.entrySet()) {
            String name = e.getKey();
            String allGrades = e.getValue().toString().replaceAll(", $", "");
            double avg = avgMap.get(name).stream().mapToDouble(Double::doubleValue).average().orElse(0);
            rows.add(new String[]{name, allGrades, String.format("%.2f", avg)});
        }
        view.getGradesTable().setItems(FXCollections.observableArrayList(rows));
    }

    private void addGrade() {
        Subject subject = view.getSubjectCombo().getValue();
        Student student = view.getStudentCombo().getValue();
        if (subject == null || student == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a subject and student.");
            return;
        }

        double grade;
        try {
            grade = Double.parseDouble(view.getGradeField());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid grade number.");
            return;
        }

        if (grade < 1 || grade > 10) {
            showAlert(Alert.AlertType.ERROR, "Error", "Grade must be between 1 and 10.");
            return;
        }

        try {
            if (gradeDAO.insertGrade(student.getId(), subject.getId(), grade)) {
                refreshGrades();
                view.clearGradeField();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void gradeClick() {
        String[] row = view.getGradesTable().getSelectionModel().getSelectedItem();
        if (row == null) return;
        
        String studentName = row[0];
        String gradesCell = row[1];
        String[] gradeArray = gradesCell.split("\\s*,\\s*");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(gradeArray[0], gradeArray);
        dialog.setTitle("Delete Grade");
        dialog.setHeaderText("Select grade to delete for " + studentName);
        dialog.setContentText("Grade:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int idx = Arrays.asList(gradeArray).indexOf(result.get());
            if (idx >= 0) {
                List<Integer> ids = gradeIdMap.get(studentName);
                if (ids != null && idx < ids.size()) {
                    try {
                        if (gradeDAO.deleteGradeById(ids.get(idx))) {
                            refreshGrades();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void refreshAbsences() throws Exception {
        Subject subject = view.getSubjectCombo().getValue();
        Student student = view.getAbsenceStudentCombo().getValue();
        if (subject == null) return;

        List<Absence> absences;
        if (student != null) {
            absences = absenceDAO.getAbsencesForStudentInSubject(student.getId(), subject.getId());
        } else {
            absences = absenceDAO.getAbsencesForSubject(subject.getId());
        }
        view.getAbsencesTable().setItems(FXCollections.observableArrayList(absences));
    }

    private void addAbsence() {
        Subject subject = view.getSubjectCombo().getValue();
        Student student = view.getAbsenceStudentCombo().getValue();
        if (subject == null || student == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a student from the filter first.");
            return;
        }

        DatePicker datePicker = new DatePicker(LocalDate.now());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Absence");
        dialog.setHeaderText("Select date for absence:");
        dialog.getDialogPane().setContent(datePicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LocalDate date = datePicker.getValue();
            if (date != null) {
                try {
                    absenceDAO.insertAbsence(student.getId(), subject.getId(), date);
                    refreshAbsences();
                } catch (IllegalArgumentException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteAbsence() {
        Absence selected = view.getAbsencesTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            if (absenceDAO.deleteAbsenceById(selected.getId())) {
                refreshAbsences();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void motivateAbsence() {
        Absence selected = view.getAbsencesTable().getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            if (absenceDAO.motivateAbsence(selected.getId())) {
                refreshAbsences();
            }
        } catch (Exception e) {
            e.printStackTrace();
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