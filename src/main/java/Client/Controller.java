package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Network net;
    private ThreadServerConnection serverConnection;

    @FXML
    private Button selectBtn, newEmailBtn, logoutBtn, sendedEmailsBtn;

    @FXML
    private ListView<Email> listView;

    @FXML
    private Label user, serverStatus;



    @FXML
    void cancelEmail(ActionEvent event){
        net = new Network();
        int selectedEmail = listView.getSelectionModel().getSelectedIndex();
        if(selectedEmail != -1){
            Email email = listView.getItems().get(selectedEmail);
            boolean success = net.deleteEmail(email);
            if (success) listView.getItems().remove(selectedEmail);
        }
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        net = new Network();
        Socket done = net.getSocket();
            net.logout(Main.getCurrentUser().getName());
            if (done != null) {
                serverConnection.closeThread();
                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                Main.changeScene(stage, "login", "Email box login");
            }
    }

    @FXML
    void sendedEmails(ActionEvent event) throws IOException {
        Stage stage = (Stage) sendedEmailsBtn.getScene().getWindow();
        Main.changeScene(stage, "sendedEmails", "Email service");
    }

    @FXML
    void newEmail(ActionEvent event) throws IOException {
        Stage stage = (Stage) newEmailBtn.getScene().getWindow();
        Main.changeScene(stage, "newEmail", "New message");
        serverConnection.closeThread();
    }

    @FXML
    public void visualizeEmail(ActionEvent event) throws IOException {
        Email item = listView.getSelectionModel().getSelectedItem();
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
        user.setText(Main.getCurrentUser().getName());
        listView.setItems(Main.getCurrentUser().getUserEmails());
        listView.setCellFactory(lv -> new ListCell<Email>() {
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
            serverConnection = new ThreadServerConnection(Main.getCurrentUser().getName(), this.serverStatus, this.listView);
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