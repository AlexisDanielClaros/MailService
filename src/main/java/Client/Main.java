package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static User currentUser;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Email box login");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void changeScene(Stage stage, String file, String stageTitle) throws IOException {
        stage.setTitle(stageTitle);
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + file + ".fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void changeSceneWithController(Stage stage, Parent root, String stageTitle) throws IOException {
        Scene scene;
        if(stageTitle.equals("Error") || stageTitle.equals("Notify")) scene = new Scene(root, 400, 250);
        else scene = new Scene(root, 600, 400);
        stage.setTitle(stageTitle);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
