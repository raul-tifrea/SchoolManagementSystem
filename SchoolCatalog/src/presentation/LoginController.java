package presentation;

import dataaccess.UserDAO;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.User;

import java.sql.SQLException;

public class LoginController {
    private Stage stage;
    private LoginView view;
    private UserDAO userDAO;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();
        this.userDAO = new UserDAO();

        Scene scene = view.getScene();
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("School Catalog - Login");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        initListeners();
    }

    private void initListeners() {
        view.getLoginButton().setOnAction(e -> login());
        

        view.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login();
            }
        });
    }

    private void login() {
        String username = view.getUsername();
        String password = view.getPassword();
        String role = view.getRole().toLowerCase();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both username and password.");
            return;
        }

        try {
            User user = userDAO.login(username, password, role);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username, password, or role.");
            } else {
                openRoleView(role, user.getId());
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openRoleView(String role, int userId) {
        try {
            if ("admin".equals(role)) {
                AdminView adminView = new AdminView();
                new AdminController(adminView, stage, userId);
            } else if ("teacher".equals(role)) {
                TeacherView teacherView = new TeacherView();
                new TeacherController(teacherView, stage, userId);
            } else if ("student".equals(role)) {
                StudentView studentView = new StudentView();
                new StudentController(studentView, stage, userId);
            }
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + ex.getMessage());
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
