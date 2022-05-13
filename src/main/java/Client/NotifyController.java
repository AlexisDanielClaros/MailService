package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class NotifyController {
    @FXML
    private Label notifyField;


    public void setNotifyField(String notify) {
        this.notifyField.setText(notify);
    }

    public static void generateNotify(String notify) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/notify.fxml"));
        Parent root = loader.load();
        NotifyController notifyController = loader.getController();
        notifyController.setNotifyField(notify);
        Main.changeSceneWithController(stage, root, "Notify");
    }
}
