package presentation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoginView {
    private Scene scene;
    
    private ComboBox<String> roleCombo;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("sidebar");
        
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(350);
        card.setMaxHeight(400);
        card.setPadding(new Insets(30));
        
        Label titleLabel = new Label("School Catalog");
        titleLabel.getStyleClass().add("dark-title-label");
        
        Label subtitleLabel = new Label("Please sign in");
        subtitleLabel.setStyle("-fx-text-fill: #666666; -fx-padding: 0 0 15 0;");
        
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "Teacher", "Student");
        roleCombo.setValue("Student");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.getStyleClass().add("combo-box");
        
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");
        
        loginButton = new Button("Sign In");
        loginButton.getStyleClass().addAll("button", "btn-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        
        card.getChildren().addAll(titleLabel, subtitleLabel, roleCombo, usernameField, passwordField, loginButton);
        
        root.setCenter(card);
        
        scene = new Scene(root, 600, 500);

    }

    public Scene getScene() {
        return scene;
    }

    public String getRole() {
        return roleCombo.getValue();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
