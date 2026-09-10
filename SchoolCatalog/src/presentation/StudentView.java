package presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Absence;
import model.Grade;
import model.Subject;

public class StudentView {
    private Scene scene;
    private Button logoutButton;

    // Grades tab
    private ComboBox<Subject> subjectBox;
    private TableView<Grade> gradesTable;
    private Label gpaLabel;
    private HBox gpaBanner;

    // Absences tab
    private ComboBox<Subject> absenceSubjectBox;
    private TableView<Absence> absencesTable;
    private Label absenceSummaryLabel;

    public StudentView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Top bar
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("header-bar");
        topBar.setPadding(new Insets(15, 20, 15, 20));
        
        Label titleLabel = new Label("Student Dashboard");
        titleLabel.getStyleClass().add("title-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button", "btn-danger");
        
        topBar.getChildren().addAll(titleLabel, spacer, logoutButton);
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

        // GPA Banner
        gpaBanner = new HBox();
        gpaBanner.setAlignment(Pos.CENTER);
        gpaBanner.setPadding(new Insets(15));
        gpaBanner.getStyleClass().add("gpa-medium");
        
        gpaLabel = new Label("GPA: —");
        gpaLabel.getStyleClass().add("title-label");
        gpaBanner.getChildren().add(gpaLabel);
        
        panel.setTop(gpaBanner);

        // Table
        gradesTable = new TableView<>();
        gradesTable.getStyleClass().add("table-view");
        
        TableColumn<Grade, String> valCol = new TableColumn<>("Grade");
        valCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getValue())));
        
        gradesTable.getColumns().add(valCol);
        gradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(20));
        tableContainer.getChildren().add(gradesTable);
        VBox.setVgrow(gradesTable, Priority.ALWAYS);
        panel.setCenter(tableContainer);

        // Bottom subject selector
        HBox bottom = new HBox(15);
        bottom.setPadding(new Insets(20));
        bottom.getStyleClass().add("card");
        
        subjectBox = new ComboBox<>();
        subjectBox.getStyleClass().add("combo-box");
        subjectBox.setPrefWidth(200);
        
        bottom.getChildren().addAll(makeDarkLabel("Subject:"), subjectBox);
        panel.setBottom(bottom);

        return panel;
    }

    private BorderPane buildAbsencesTab() {
        BorderPane panel = new BorderPane();

        // Summary Banner
        HBox summaryBanner = new HBox();
        summaryBanner.setAlignment(Pos.CENTER);
        summaryBanner.setPadding(new Insets(15));
        summaryBanner.setStyle("-fx-background-color: #3E1978;");
        
        absenceSummaryLabel = new Label("Total: 0  |  Motivated: 0  |  Unexcused: 0");
        absenceSummaryLabel.getStyleClass().add("title-label");
        summaryBanner.getChildren().add(absenceSummaryLabel);
        
        panel.setTop(summaryBanner);

        // Table
        absencesTable = new TableView<>();
        absencesTable.getStyleClass().add("table-view");
        
        TableColumn<Absence, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Absence, String> motCol = new TableColumn<>("Motivated");
        motCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isMotivated() ? "✓ Yes" : "✗ No"));
        
        absencesTable.getColumns().addAll(dateCol, motCol);
        absencesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox tableContainer = new VBox();
        tableContainer.setPadding(new Insets(20));
        tableContainer.getChildren().add(absencesTable);
        VBox.setVgrow(absencesTable, Priority.ALWAYS);
        panel.setCenter(tableContainer);

        // Bottom subject selector
        HBox bottom = new HBox(15);
        bottom.setPadding(new Insets(20));
        bottom.getStyleClass().add("card");
        
        absenceSubjectBox = new ComboBox<>();
        absenceSubjectBox.getStyleClass().add("combo-box");
        absenceSubjectBox.setPrefWidth(200);
        
        bottom.getChildren().addAll(makeDarkLabel("Subject:"), absenceSubjectBox);
        panel.setBottom(bottom);

        return panel;
    }

    private Label makeDarkLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #230F41;");
        return l;
    }

    public Scene getScene() { return scene; }
    public Button getLogoutButton() { return logoutButton; }
    
    public ComboBox<Subject> getSubjectBox() { return subjectBox; }
    public TableView<Grade> getGradesTable() { return gradesTable; }
    public Label getGpaLabel() { return gpaLabel; }
    public HBox getGpaBanner() { return gpaBanner; }
    
    public ComboBox<Subject> getAbsenceSubjectBox() { return absenceSubjectBox; }
    public TableView<Absence> getAbsencesTable() { return absencesTable; }
    public Label getAbsenceSummaryLabel() { return absenceSummaryLabel; }
}
