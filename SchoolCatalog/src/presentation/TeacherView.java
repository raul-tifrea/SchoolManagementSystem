package presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Absence;
import model.Grade;
import model.Student;
import model.Subject;

import java.util.List;

public class TeacherView {
    private Scene scene;


    private ComboBox<Subject> subjectCombo;
    private Button logoutButton, changePasswordButton;


    private TableView<String[]> gradesTable;
    private ComboBox<Student> studentCombo;
    private TextField gradeField;
    private Button addGradeButton;


    private TableView<Absence> absencesTable;
    private ComboBox<Student> absenceStudentCombo;
    private Button addAbsenceButton, deleteAbsenceButton, motivateAbsenceButton;

    public TeacherView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");


        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("header-bar");
        topBar.setPadding(new Insets(15, 20, 15, 20));
        
        Label titleLabel = new Label("Teacher Dashboard");
        titleLabel.getStyleClass().add("title-label");
        
        Label subLabel = new Label("Subject:");
        subLabel.getStyleClass().add("white-label");
        
        subjectCombo = new ComboBox<>();
        subjectCombo.getStyleClass().add("combo-box");
        subjectCombo.setPrefWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button", "btn-danger");
        
        changePasswordButton = new Button("🔑 Change Password");
        changePasswordButton.getStyleClass().addAll("button", "btn-primary");

        topBar.getChildren().addAll(titleLabel, subLabel, subjectCombo, spacer, changePasswordButton, logoutButton);
        root.setTop(topBar);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab gradesTab = new Tab("📝 Grades");
        gradesTab.setContent(buildGradesTab());
        
        Tab absencesTab = new Tab("📅 Absences");
        absencesTab.setContent(buildAbsencesTab());
        
        tabs.getTabs().addAll(gradesTab, absencesTab);
        root.setCenter(tabs);

        scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    private BorderPane buildGradesTab() {
        BorderPane panel = new BorderPane();


        HBox form = new HBox(15);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("card");
        
        studentCombo = new ComboBox<>();
        studentCombo.getStyleClass().add("combo-box");
        studentCombo.setPrefWidth(200);
        
        gradeField = new TextField();
        gradeField.getStyleClass().add("text-field");
        gradeField.setPromptText("Grade (1-10)");
        gradeField.setPrefWidth(100);
        
        addGradeButton = new Button("＋ Add Grade");
        addGradeButton.getStyleClass().addAll("button", "btn-success");
        
        Label helpLabel = new Label(" (Double click a row to delete a grade)");
        helpLabel.setStyle("-fx-text-fill: #888888; -fx-padding: 5 0 0 0;");
        
        form.getChildren().addAll(
            new VBox(5, makeDarkLabel("Student:"), studentCombo),
            new VBox(5, makeDarkLabel("Grade:"), gradeField),
            new VBox(5, new Label(), addGradeButton),
            new VBox(5, new Label(), helpLabel)
        );
        panel.setTop(form);


        gradesTable = new TableView<>();
        gradesTable.getStyleClass().add("table-view");
        
        TableColumn<String[], String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        studentCol.setPrefWidth(200);
        
        TableColumn<String[], String> gradesCol = new TableColumn<>("Grades");
        gradesCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        gradesCol.setPrefWidth(300);
        
        TableColumn<String[], String> avgCol = new TableColumn<>("Average");
        avgCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        avgCol.setPrefWidth(150);
        
        gradesTable.getColumns().addAll(studentCol, gradesCol, avgCol);
        gradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(20));
        tableContainer.getChildren().add(gradesTable);
        VBox.setVgrow(gradesTable, Priority.ALWAYS);
        
        panel.setCenter(tableContainer);
        return panel;
    }

    private BorderPane buildAbsencesTab() {
        BorderPane panel = new BorderPane();


        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(20));
        filterBox.getStyleClass().add("card");
        
        absenceStudentCombo = new ComboBox<>();
        absenceStudentCombo.getStyleClass().add("combo-box");
        absenceStudentCombo.setPrefWidth(200);
        
        addAbsenceButton = new Button("＋ Add Absence");
        addAbsenceButton.getStyleClass().addAll("button", "btn-success");
        
        deleteAbsenceButton = new Button("✕ Delete");
        deleteAbsenceButton.getStyleClass().addAll("button", "btn-danger");
        
        motivateAbsenceButton = new Button("✓ Mark Motivated");
        motivateAbsenceButton.getStyleClass().addAll("button", "btn-primary");
        
        filterBox.getChildren().addAll(
            new VBox(5, makeDarkLabel("Filter by student:"), absenceStudentCombo),
            new VBox(5, new Label(), new HBox(10, addAbsenceButton, deleteAbsenceButton, motivateAbsenceButton))
        );
        panel.setTop(filterBox);


        absencesTable = new TableView<>();
        absencesTable.getStyleClass().add("table-view");
        
        TableColumn<Absence, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentCol.setPrefWidth(200);
        
        TableColumn<Absence, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(200);
        
        TableColumn<Absence, String> motCol = new TableColumn<>("Motivated");
        motCol.setCellValueFactory(cellData -> {
            boolean motivated = cellData.getValue().isMotivated();
            return new SimpleStringProperty(motivated ? "✓ Yes" : "✗ No");
        });
        motCol.setPrefWidth(150);
        
        absencesTable.getColumns().addAll(studentCol, dateCol, motCol);
        absencesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        tableContainer.getChildren().add(absencesTable);
        VBox.setVgrow(absencesTable, Priority.ALWAYS);
        
        panel.setCenter(tableContainer);
        return panel;
    }

    private Label makeDarkLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #230F41;");
        return l;
    }

    public Scene getScene() { return scene; }
    
    public ComboBox<Subject> getSubjectCombo() { return subjectCombo; }
    public Button getLogoutButton() { return logoutButton; }
    public Button getChangePasswordButton() { return changePasswordButton; }
    

    public TableView<String[]> getGradesTable() { return gradesTable; }
    public ComboBox<Student> getStudentCombo() { return studentCombo; }
    public String getGradeField() { return gradeField.getText().trim(); }
    public void clearGradeField() { gradeField.clear(); }
    public Button getAddGradeButton() { return addGradeButton; }
    

    public TableView<Absence> getAbsencesTable() { return absencesTable; }
    public ComboBox<Student> getAbsenceStudentCombo() { return absenceStudentCombo; }
    public Button getAddAbsenceButton() { return addAbsenceButton; }
    public Button getDeleteAbsenceButton() { return deleteAbsenceButton; }
    public Button getMotivateAbsenceButton() { return motivateAbsenceButton; }
}
