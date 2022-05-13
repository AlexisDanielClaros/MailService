package Server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    ModelServer model;
    ThreadPool ts;

    @FXML
    private ListView<String> logView;

    @FXML
    private ToggleButton disconnectServer;

    @FXML
    void disconnectServer(ActionEvent event) throws IOException {
       if (disconnectServer.isSelected()){
           ts.terminate();
           disconnectServer.setText("Server offline");
           disconnectServer.setStyle("-fx-text-fill: red");
       }
       else {
           ts = new ThreadPool(model);
           ts.start();
           disconnectServer.setText("Server online");
           disconnectServer.setStyle("-fx-text-fill: #26dd05");
       }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.model = new ModelServer();
        logView.setItems(model.getLogs());
        ts = new ThreadPool(model);
        ts.start();
    }
}
