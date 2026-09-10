package start;

import javafx.application.Application;
import javafx.stage.Stage;
import presentation.LoginController;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        new LoginController(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
