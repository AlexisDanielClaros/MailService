package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EmailController {

    @FXML
    private Button backBtn, forwardBtn, replyBtn;

    @FXML
    private ComboBox<String> containersBox, containers;

    @FXML
    private TextField contField, argField, messageField;

    @FXML
    private Label arg, from, message, date;

    @FXML
    void addContainer(ActionEvent event) throws IOException {
        String container = contField.getText();
        if(checkData(container)) {
            containersBox.getItems().add(container);
            contField.setText(null);
        }
        contField.clear();
    }

    @FXML
    void deleteContainer(ActionEvent event) throws IOException {
        String item = containersBox.getSelectionModel().getSelectedItem();
        containersBox.getItems().remove(item);
    }

    @FXML
    void prevScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) backBtn.getScene().getWindow();
        Main.changeScene(stage, "home", "Email service");
    }

    @FXML
    void sendEmail(ActionEvent event) throws IOException{
        Network net = new Network();
       String arg = this.argField.getText();
       String from = Main.getCurrentUser().getName();
       String message = this.messageField.getText();
       DateTimeFormatter time = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
       String date = time.format(LocalDateTime.now());
       ArrayList<String> recipients = new ArrayList<>(this.containersBox.getItems());
       Email email = new Email(from, arg, message, date, recipients);
       net.sendNewEmail(email);
    }

    @FXML
    void reply(ActionEvent event) throws IOException {
        replyEmail(event, false);
    }

    @FXML
    void replyAll(ActionEvent event) throws IOException {
        replyEmail(event, true);
    }

    private void replyEmail(ActionEvent event, boolean all) throws IOException {
        Stage stage = (Stage) replyBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/newEmail.fxml"));
        Parent root = loader.load();
        EmailController emailController = loader.getController();
        if (all) emailController.containersBox.setItems(this.containers.getItems()); // copy the recipients
        emailController.containersBox.getItems().add(this.from.getText()); // copy just the sender
        emailController.argField.setText(this.arg.getText());
        Main.changeSceneWithController(stage, root, "Email service");
    }

    @FXML
    void forwardEmail(ActionEvent event) throws IOException {
        Stage stage = (Stage) forwardBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/newEmail.fxml"));
        Parent root = loader.load();
        EmailController emailController = loader.getController();
        emailController.argField.setText(arg.getText());
        emailController.messageField.setText(message.getText());
        Main.changeSceneWithController(stage, root, "Email service");
    }

    private boolean checkData(String email) throws IOException{
        String error = null;
        if(email.isEmpty())
            error = "Empty field";
        else if(!(email.contains("@") && email.contains(".") && email.contains("com")))
            error = "Wrong parameter";

        if( error != null ) { // Error found
           ErrorController.generateError(error);
           return false;
        }
        else return true;
    }

    public void setArg(String arg) {
        this.arg.setText(arg);
    }

    public void setFrom(String from) {
        this.from.setText(from);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setDate(String date) {
        this.date.setText(date);
    }

    public void setRecipients(ArrayList<String> recipients) {
        for (String recipient : recipients)
            this.containers.getItems().add(recipient);
    }
}
