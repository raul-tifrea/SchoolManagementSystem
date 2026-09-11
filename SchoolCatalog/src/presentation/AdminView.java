package presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Student;
import model.Subject;
import model.User;

import java.util.List;

public class AdminView {
    private Scene scene;


    private TableView<User> usersTable;


    private TextField usernameField, passwordField, nameField, emailField;
    private ComboBox<String> roleBox;
    private Spinner<Integer> yearSpinner;
    private ComboBox<String> groupBox;
    private Label yearLabel, groupLabel;
    private Button addButton, deleteButton, editButton, setSubjectTeacher, logoutButton, changePasswordButton;

    private ComboBox<Subject> enrollSubjectBox;
    private ComboBox<Student> enrollStudentBox;
    private Button enrollButton, enrollAllButton, removeEnrollButton;
    private TableView<String[]> enrollmentsTable;

    private TableView<String[]> subjectsTable;
    private Button deleteSubjectButton, reassignSubjectButton;

    public AdminView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");


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

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab usersTab = new Tab("👥 Users");
        usersTab.setContent(buildUsersTab());
        
        Tab enrollTab = new Tab("📋 Enrollments");
        enrollTab.setContent(buildEnrollmentsTab());
        
        Tab subjectsTab = new Tab("📚 Subjects");
        subjectsTab.setContent(buildSubjectsTab());
        
        tabs.getTabs().addAll(usersTab, enrollTab, subjectsTab);
        root.setCenter(tabs);

        scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }

    private BorderPane buildUsersTab() {
        BorderPane panel = new BorderPane();


        VBox sidebar = new VBox(15);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        Label formTitle = new Label("Add New User");
        formTitle.getStyleClass().add("title-label");
        sidebar.getChildren().add(formTitle);

        usernameField = new TextField(); usernameField.getStyleClass().add("text-field");
        passwordField = new PasswordField(); passwordField.getStyleClass().add("password-field");
        nameField = new TextField(); nameField.getStyleClass().add("text-field");
        emailField = new TextField(); emailField.getStyleClass().add("text-field");
        
        roleBox = new ComboBox<>();
        roleBox.getItems().addAll("student", "teacher");
        roleBox.getStyleClass().add("combo-box");
        roleBox.setMaxWidth(Double.MAX_VALUE);
        roleBox.setValue("student");

        yearSpinner = new Spinner<>(9, 12, 9); yearSpinner.getStyleClass().add("spinner");
        yearSpinner.setMaxWidth(Double.MAX_VALUE);
        groupBox = new ComboBox<>(); 
        groupBox.getItems().addAll("A", "B", "C");
        groupBox.setValue("A");
        groupBox.getStyleClass().add("combo-box");
        groupBox.setMaxWidth(Double.MAX_VALUE);

        yearLabel = makeLabel("Class (9-12)");
        groupLabel = makeLabel("Group (A-C)");

        sidebar.getChildren().addAll(
                makeLabeledField("Username", usernameField),
                makeLabeledField("Password", passwordField),
                makeLabeledField("Full Name", nameField),
                makeLabeledField("Email", emailField),
                makeLabeledField("Role", roleBox),
                yearLabel, yearSpinner,
                groupLabel, groupBox
        );

        addButton = new Button("＋ Add User");
        addButton.getStyleClass().addAll("button", "btn-success");
        addButton.setMaxWidth(Double.MAX_VALUE);

        deleteButton = new Button("✕ Delete Selected");
        deleteButton.getStyleClass().addAll("button", "btn-danger");
        deleteButton.setMaxWidth(Double.MAX_VALUE);

        setSubjectTeacher = new Button("📚 Assign Subject");
        setSubjectTeacher.getStyleClass().addAll("button", "btn-primary");
        setSubjectTeacher.setMaxWidth(Double.MAX_VALUE);

        editButton = new Button("✏ Edit Selected");
        editButton.getStyleClass().addAll("button", "btn-primary");
        editButton.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(addButton, editButton, deleteButton, setSubjectTeacher);

        roleBox.setOnAction(e -> {
            boolean isStudent = "student".equals(roleBox.getValue());
            yearLabel.setVisible(isStudent); yearLabel.setManaged(isStudent);
            yearSpinner.setVisible(isStudent); yearSpinner.setManaged(isStudent);
            groupLabel.setVisible(isStudent); groupLabel.setManaged(isStudent);
            groupBox.setVisible(isStudent); groupBox.setManaged(isStudent);
        });

        ScrollPane scrollSidebar = new ScrollPane(sidebar);
        scrollSidebar.setFitToWidth(true);
        scrollSidebar.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scrollSidebar.setPrefWidth(280);

        panel.setLeft(scrollSidebar);


        usersTable = new TableView<>();
        usersTable.getStyleClass().add("table-view");
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setVisible(false);
        
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(150);
        
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(100);


        TableColumn<User, String> extraCol = new TableColumn<>("Subject / Year-Group");
        extraCol.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String extra = "-";
            if (u.getSubject() != null) {
                extra = u.getSubject();
            }
            return new SimpleStringProperty(extra);
        });
        extraCol.setPrefWidth(250);
        
        usersTable.getColumns().addAll(idCol, userCol, roleCol, extraCol);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(20));
        tableContainer.getChildren().add(usersTable);
        VBox.setVgrow(usersTable, Priority.ALWAYS);
        
        panel.setCenter(tableContainer);
        return panel;
    }

    private BorderPane buildEnrollmentsTab() {
        BorderPane panel = new BorderPane();
        
        HBox topControls = new HBox(15);
        topControls.setPadding(new Insets(20));
        topControls.getStyleClass().add("card");
        
        enrollSubjectBox = new ComboBox<>(); enrollSubjectBox.getStyleClass().add("combo-box");
        enrollSubjectBox.setPrefWidth(200);
        
        enrollStudentBox = new ComboBox<>(); enrollStudentBox.getStyleClass().add("combo-box");
        enrollStudentBox.setPrefWidth(200);
        
        enrollButton = new Button("＋ Enroll"); enrollButton.getStyleClass().addAll("button", "btn-success");
        enrollAllButton = new Button("⚡ Enroll Year"); enrollAllButton.getStyleClass().addAll("button", "btn-primary");
        removeEnrollButton = new Button("✕ Remove"); removeEnrollButton.getStyleClass().addAll("button", "btn-danger");
        
        topControls.getChildren().addAll(
                new VBox(5, makeDarkLabel("Subject:"), enrollSubjectBox),
                new VBox(5, makeDarkLabel("Student:"), enrollStudentBox),
                new VBox(5, new Label(), new HBox(10, enrollButton, enrollAllButton, removeEnrollButton))
        );
        
        panel.setTop(topControls);

        enrollmentsTable = new TableView<>();
        enrollmentsTable.getStyleClass().add("table-view");
        
        TableColumn<String[], String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        studentCol.setPrefWidth(250);
        
        TableColumn<String[], String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        subjectCol.setPrefWidth(250);
        
        enrollmentsTable.getColumns().addAll(studentCol, subjectCol);
        enrollmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        tableContainer.getChildren().add(enrollmentsTable);
        VBox.setVgrow(enrollmentsTable, Priority.ALWAYS);
        
        panel.setCenter(tableContainer);
        return panel;
    }

    private BorderPane buildSubjectsTab() {
        BorderPane panel = new BorderPane();

        HBox controls = new HBox(10);
        controls.setPadding(new Insets(20));
        controls.getStyleClass().add("card");

        deleteSubjectButton = new Button("Delete Subject");
        deleteSubjectButton.getStyleClass().addAll("button", "btn-danger");

        reassignSubjectButton = new Button("Reassign Teacher");
        reassignSubjectButton.getStyleClass().addAll("button", "btn-primary");

        controls.getChildren().addAll(
            new javafx.scene.control.Label("Select a subject below, then use an action:"),
            reassignSubjectButton, deleteSubjectButton
        );
        panel.setTop(controls);

        subjectsTable = new TableView<>();
        subjectsTable.getStyleClass().add("table-view");

        TableColumn<String[], String> nameCol = new TableColumn<>("Subject");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> teacherCol = new TableColumn<>("Teacher");
        teacherCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        subjectsTable.getColumns().addAll(nameCol, yearCol, teacherCol);
        subjectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(0, 20, 20, 20));
        tableContainer.getChildren().add(subjectsTable);
        VBox.setVgrow(subjectsTable, Priority.ALWAYS);

        panel.setCenter(tableContainer);
        return panel;
    }

    private VBox makeLabeledField(String text, Control field) {
        VBox box = new VBox(5);
        box.getChildren().addAll(makeLabel(text), field);
        return box;
    }

    private Label makeLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("form-label");
        return l;
    }

    private Label makeDarkLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #230F41;");
        return l;
    }

    public Scene getScene() { return scene; }
    
    public TableView<User> getUsersTable() { return usersTable; }
    public TableView<String[]> getEnrollmentsTable() { return enrollmentsTable; }
    public TableView<String[]> getSubjectsTable() { return subjectsTable; }
    public Button getDeleteSubjectButton() { return deleteSubjectButton; }
    public Button getReassignSubjectButton() { return reassignSubjectButton; }
    
    public Button getAddButton() { return addButton; }
    public Button getDeleteButton() { return deleteButton; }
    public Button getEditButton() { return editButton; }
    public Button getSetSubjectTeacherButton() { return setSubjectTeacher; }
    public Button getEnrollButton() { return enrollButton; }
    public Button getEnrollAllButton() { return enrollAllButton; }
    public Button getRemoveEnrollButton() { return removeEnrollButton; }
    public Button getLogoutButton() { return logoutButton; }
    public Button getChangePasswordButton() { return changePasswordButton; }
    
    public ComboBox<Subject> getEnrollSubjectBox() { return enrollSubjectBox; }
    public ComboBox<Student> getEnrollStudentBox() { return enrollStudentBox; }
    
    public Subject getSelectedEnrollSubject() { return enrollSubjectBox.getValue(); }
    public Student getSelectedEnrollStudent() { return enrollStudentBox.getValue(); }
    
    public void populateSubjectBox(List<Subject> subjects) {
        enrollSubjectBox.getItems().setAll(subjects);
    }

    public void populateStudentBox(List<Student> students) {
        enrollStudentBox.getItems().setAll(students);
    }
    
    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return passwordField.getText().trim(); }
    public String getName() { return nameField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getRole() { return roleBox.getValue(); }
    public int getYear() { return yearSpinner.getValue(); }
    public String getGroup() { return groupBox.getValue(); }
    
    public void clearForm() {
        usernameField.clear(); passwordField.clear();
        nameField.clear(); emailField.clear();
        roleBox.setValue("student");
        yearSpinner.getValueFactory().setValue(9);
        groupBox.setValue("A");
    }
}
