package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SndEmailController implements Initializable {
    private Network net;
    private ThreadServerConnection serverConnection;

    @FXML
    private Button selectBtn,  returnHome;

    @FXML
    private ListView<Email> listViewSnd;

    @FXML
    private Label serverStatus;



    @FXML
    void cancelEmail(ActionEvent event){
        net = new Network();
        int selectedEmail = listViewSnd.getSelectionModel().getSelectedIndex();
        if(selectedEmail != -1){
            Email email = listViewSnd.getItems().get(selectedEmail);
            boolean success = net.deleteEmail(email);
            if (success) listViewSnd.getItems().remove(selectedEmail);
        }
    }

    @FXML
    void prevScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) returnHome.getScene().getWindow();
        Main.changeScene(stage, "home", "Email service");
    }

    @FXML
    public void visualizeEmail(ActionEvent event) throws IOException {
        Email item = listViewSnd.getSelectionModel().getSelectedItem();
        if (item != null) {
            Stage stage = (Stage) selectBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/email.fxml"));
            Parent root = loader.load();
            EmailController emailController = loader.getController();
            emailController.setFrom(item.getSender());
            emailController.setArg(item.getArgument());
            emailController.setMessage(item.getMessage());
            emailController.setDate(item.getDate());
            emailController.setRecipients(item.getRecipients());
            Main.changeSceneWithController(stage, root, "Email content");
            serverConnection.closeThread();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewSnd.setItems(Main.getCurrentUser().getUserEmailsSnd());
        listViewSnd.setCellFactory(lv -> new ListCell<Email>() {
            @Override
            public void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    String text = item.getMessage();
                    if(text.length() > 30) text = text.substring(0, 30) + "...";
                    setText("From: " + item.getSender() + "\t" + "Arg: " + item.getArgument() + "\t" + "Message: " + text);
                }
            }
        });

        if(serverConnection == null) {
            serverConnection = new ThreadServerConnection(Main.getCurrentUser().getName(), this.serverStatus, this.listViewSnd);
            try {
                serverConnection.start();
            } catch (Exception e) {
                serverConnection.closeThread();
            }
        }
    }

    public static void addToListView(Email email){
        Main.getCurrentUser().getUserEmails().add(0,email);
    }

}
