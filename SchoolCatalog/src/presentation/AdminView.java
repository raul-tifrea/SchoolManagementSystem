package presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Student;
import model.Subject;

import java.util.List;

public class AdminView {
    private Scene scene;
    private BorderPane root;
    private StackPane contentArea;

    // Navigation Buttons
    private Button navStudents, navTeachers, navSubjects, navEnrollments;
    private Button logoutButton, changePasswordButton;

    // Views
    private BorderPane studentsView, teachersView, subjectsView, enrollmentsView;

    // Students Tab
    private TableView<String[]> studentsTable;
    private Button addStudentButton, editStudentButton, deleteStudentButton;

    // Teachers Tab
    private TableView<String[]> teachersTable;
    private Button addTeacherButton, editTeacherButton, deleteTeacherButton;

    // Subjects Tab
    private TableView<String[]> subjectsTable;
    private Button addSubjectButton, reassignSubjectButton, deleteSubjectButton;

    // Enrollments Tab
    private ComboBox<Subject> enrollSubjectBox;
    private ComboBox<Student> enrollStudentBox;
    private Button enrollButton, enrollAllButton, removeEnrollButton;
    private TableView<String[]> enrollmentsTable;

    public AdminView() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Bar
        HBox topBar = new HBox();
        topBar.getStyleClass().add("header-bar");
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.getStyleClass().add("title-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button", "btn-danger");
        changePasswordButton = new Button("🔑 Change Password");
        changePasswordButton.getStyleClass().addAll("button", "btn-primary");
        topBar.getChildren().addAll(titleLabel, spacer, changePasswordButton, logoutButton);
        root.setTop(topBar);

        // Left Sidebar
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);

        navStudents = new Button("🎓 Students"); navStudents.getStyleClass().add("sidebar-button");
        navTeachers = new Button("🧑‍🏫 Teachers"); navTeachers.getStyleClass().add("sidebar-button");
        navSubjects = new Button("📚 Subjects"); navSubjects.getStyleClass().add("sidebar-button");
        navEnrollments = new Button("📋 Enrollments"); navEnrollments.getStyleClass().add("sidebar-button");

        navStudents.setMaxWidth(Double.MAX_VALUE);
        navTeachers.setMaxWidth(Double.MAX_VALUE);
        navSubjects.setMaxWidth(Double.MAX_VALUE);
        navEnrollments.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(navStudents, navTeachers, navSubjects, navEnrollments);
        root.setLeft(sidebar);

        // Center Content Area
        contentArea = new StackPane();
        root.setCenter(contentArea);

        // Build all views
        buildStudentsView();
        buildTeachersView();
        buildSubjectsView();
        buildEnrollmentsView();

        // Default view
        setContentPane(studentsView);

        scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    public void setContentPane(Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    private void buildStudentsView() {
        studentsView = new BorderPane();
        HBox top = new HBox(15);
        top.setPadding(new Insets(20));
        top.getStyleClass().add("card");

        addStudentButton = new Button("＋ Add Student"); addStudentButton.getStyleClass().addAll("button", "btn-success");
        editStudentButton = new Button("✏ Edit Student"); editStudentButton.getStyleClass().addAll("button", "btn-primary");
        deleteStudentButton = new Button("✕ Delete"); deleteStudentButton.getStyleClass().addAll("button", "btn-danger");

        top.getChildren().addAll(new Label("Manage Students:"), addStudentButton, editStudentButton, deleteStudentButton);
        studentsView.setTop(top);

        studentsTable = new TableView<>();
        studentsTable.getStyleClass().add("table-view");
        
        TableColumn<String[], String> userCol = new TableColumn<>("Username"); userCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        TableColumn<String[], String> nameCol = new TableColumn<>("Name"); nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        TableColumn<String[], String> emailCol = new TableColumn<>("Email"); emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        TableColumn<String[], String> yearCol = new TableColumn<>("Year"); yearCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));
        TableColumn<String[], String> groupCol = new TableColumn<>("Group"); groupCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));

        studentsTable.getColumns().addAll(userCol, nameCol, emailCol, yearCol, groupCol);
        studentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox container = new VBox(); container.setPadding(new Insets(0, 20, 20, 20));
        container.getChildren().add(studentsTable); VBox.setVgrow(studentsTable, Priority.ALWAYS);
        studentsView.setCenter(container);
    }

    private void buildTeachersView() {
        teachersView = new BorderPane();
        HBox top = new HBox(15);
        top.setPadding(new Insets(20));
        top.getStyleClass().add("card");

        addTeacherButton = new Button("＋ Add Teacher"); addTeacherButton.getStyleClass().addAll("button", "btn-success");
        editTeacherButton = new Button("✏ Edit Teacher"); editTeacherButton.getStyleClass().addAll("button", "btn-primary");
        deleteTeacherButton = new Button("✕ Delete"); deleteTeacherButton.getStyleClass().addAll("button", "btn-danger");

        top.getChildren().addAll(new Label("Manage Teachers:"), addTeacherButton, editTeacherButton, deleteTeacherButton);
        teachersView.setTop(top);

        teachersTable = new TableView<>();
        teachersTable.getStyleClass().add("table-view");
        
        TableColumn<String[], String> userCol = new TableColumn<>("Username"); userCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        TableColumn<String[], String> nameCol = new TableColumn<>("Name"); nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        TableColumn<String[], String> emailCol = new TableColumn<>("Email"); emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));
        TableColumn<String[], String> subsCol = new TableColumn<>("Subjects"); subsCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        teachersTable.getColumns().addAll(userCol, nameCol, emailCol, subsCol);
        teachersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox container = new VBox(); container.setPadding(new Insets(0, 20, 20, 20));
        container.getChildren().add(teachersTable); VBox.setVgrow(teachersTable, Priority.ALWAYS);
        teachersView.setCenter(container);
    }

    private void buildSubjectsView() {
        subjectsView = new BorderPane();
        HBox top = new HBox(15);
        top.setPadding(new Insets(20));
        top.getStyleClass().add("card");

        addSubjectButton = new Button("＋ Add Subject"); addSubjectButton.getStyleClass().addAll("button", "btn-success");
        reassignSubjectButton = new Button("🔄 Reassign Teacher"); reassignSubjectButton.getStyleClass().addAll("button", "btn-primary");
        deleteSubjectButton = new Button("✕ Delete Subject"); deleteSubjectButton.getStyleClass().addAll("button", "btn-danger");

        top.getChildren().addAll(new Label("Manage Subjects:"), addSubjectButton, reassignSubjectButton, deleteSubjectButton);
        subjectsView.setTop(top);

        subjectsTable = new TableView<>();
        subjectsTable.getStyleClass().add("table-view");

        TableColumn<String[], String> nameCol = new TableColumn<>("Subject"); nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        TableColumn<String[], String> yearCol = new TableColumn<>("Year"); yearCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        TableColumn<String[], String> teacherCol = new TableColumn<>("Teacher"); teacherCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        subjectsTable.getColumns().addAll(nameCol, yearCol, teacherCol);
        subjectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox container = new VBox(); container.setPadding(new Insets(0, 20, 20, 20));
        container.getChildren().add(subjectsTable); VBox.setVgrow(subjectsTable, Priority.ALWAYS);
        subjectsView.setCenter(container);
    }

    private void buildEnrollmentsView() {
        enrollmentsView = new BorderPane();
        HBox top = new HBox(15);
        top.setPadding(new Insets(20));
        top.getStyleClass().add("card");

        enrollSubjectBox = new ComboBox<>(); enrollSubjectBox.getStyleClass().add("combo-box"); enrollSubjectBox.setPrefWidth(200);
        enrollStudentBox = new ComboBox<>(); enrollStudentBox.getStyleClass().add("combo-box"); enrollStudentBox.setPrefWidth(200);
        enrollButton = new Button("＋ Enroll"); enrollButton.getStyleClass().addAll("button", "btn-success");
        enrollAllButton = new Button("⚡ Enroll Year"); enrollAllButton.getStyleClass().addAll("button", "btn-primary");
        removeEnrollButton = new Button("✕ Remove"); removeEnrollButton.getStyleClass().addAll("button", "btn-danger");

        top.getChildren().addAll(
                new VBox(5, makeDarkLabel("Subject:"), enrollSubjectBox),
                new VBox(5, makeDarkLabel("Student:"), enrollStudentBox),
                new VBox(5, new Label(), new HBox(10, enrollButton, enrollAllButton, removeEnrollButton))
        );
        enrollmentsView.setTop(top);

        enrollmentsTable = new TableView<>();
        enrollmentsTable.getStyleClass().add("table-view");

        TableColumn<String[], String> studentCol = new TableColumn<>("Student"); studentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        TableColumn<String[], String> subjectCol = new TableColumn<>("Subject"); subjectCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));

        enrollmentsTable.getColumns().addAll(studentCol, subjectCol);
        enrollmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox container = new VBox(); container.setPadding(new Insets(0, 20, 20, 20));
        container.getChildren().add(enrollmentsTable); VBox.setVgrow(enrollmentsTable, Priority.ALWAYS);
        enrollmentsView.setCenter(container);
    }

    private Label makeDarkLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #230F41;");
        return l;
    }

    // Getters
    public Scene getScene() { return scene; }
    
    // Nav
    public Button getNavStudentsButton() { return navStudents; }
    public Button getNavTeachersButton() { return navTeachers; }
    public Button getNavSubjectsButton() { return navSubjects; }
    public Button getNavEnrollmentsButton() { return navEnrollments; }
    public BorderPane getStudentsView() { return studentsView; }
    public BorderPane getTeachersView() { return teachersView; }
    public BorderPane getSubjectsView() { return subjectsView; }
    public BorderPane getEnrollmentsView() { return enrollmentsView; }

    // Students
    public TableView<String[]> getStudentsTable() { return studentsTable; }
    public Button getAddStudentButton() { return addStudentButton; }
    public Button getEditStudentButton() { return editStudentButton; }
    public Button getDeleteStudentButton() { return deleteStudentButton; }

    // Teachers
    public TableView<String[]> getTeachersTable() { return teachersTable; }
    public Button getAddTeacherButton() { return addTeacherButton; }
    public Button getEditTeacherButton() { return editTeacherButton; }
    public Button getDeleteTeacherButton() { return deleteTeacherButton; }

    // Subjects
    public TableView<String[]> getSubjectsTable() { return subjectsTable; }
    public Button getAddSubjectButton() { return addSubjectButton; }
    public Button getReassignSubjectButton() { return reassignSubjectButton; }
    public Button getDeleteSubjectButton() { return deleteSubjectButton; }

    // Enrollments
    public TableView<String[]> getEnrollmentsTable() { return enrollmentsTable; }
    public Button getEnrollButton() { return enrollButton; }
    public Button getEnrollAllButton() { return enrollAllButton; }
    public Button getRemoveEnrollButton() { return removeEnrollButton; }
    public ComboBox<Subject> getEnrollSubjectBox() { return enrollSubjectBox; }
    public ComboBox<Student> getEnrollStudentBox() { return enrollStudentBox; }
    public Subject getSelectedEnrollSubject() { return enrollSubjectBox.getValue(); }
    public Student getSelectedEnrollStudent() { return enrollStudentBox.getValue(); }

    public void populateSubjectBox(List<Subject> subjects) { enrollSubjectBox.getItems().setAll(subjects); }
    public void populateStudentBox(List<Student> students) { enrollStudentBox.getItems().setAll(students); }

    public Button getLogoutButton() { return logoutButton; }
    public Button getChangePasswordButton() { return changePasswordButton; }
}
