package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    Network net;

    @FXML
    private PasswordField password;

    @FXML
    private TextField email;

    @FXML
    void login(ActionEvent event) throws IOException{
        String email = this.email.getText();
        String password = this.password.getText();
        boolean correct = checkLoginData(email, password);
        if (correct){
            net = new Network();
            Boolean userFound = net.userLogin(email);
            if(userFound){
                Stage stage = (Stage) this.email.getScene().getWindow();
                Main.changeScene(stage, "home", "Email service");
            }
        }
    }


    private boolean checkLoginData(String email, String password) throws IOException{
        String error = null;
        if(email.isEmpty() || password.isEmpty())
            error = "Empty field";
        else if(!email.matches("^[a-z\\.0-9]+@+[a-zA-Z0-9]+(\\.+[a-zA-Z]{2,})$"))
            error = "Wrong parameters";

        if( error != null ) { // Error found
            ErrorController.generateError(error);
            this.email.clear();
            this.password.clear();
            return false;
        }
        return true;
    }
}

