package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ErrorController {

    @FXML
    private Label errorField;


    public void setErrorField(String errorMessage) {
        errorField.setText(errorMessage);
    }

    public static void generateError(String error) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/error.fxml"));
        Parent root = loader.load();
        ErrorController errorController = loader.getController();
        errorController.setErrorField(error);
        Main.changeSceneWithController(stage, root, "Error");
    }

}