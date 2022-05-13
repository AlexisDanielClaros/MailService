package Client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private transient String name;
    private ObservableList<Email> userEmails;
    private ObservableList<Email> userEmailsSnd;

    public User(String name){
        this.name=name;
        userEmails = FXCollections.observableArrayList();
        userEmailsSnd = FXCollections.observableArrayList();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public void removeEmail(int index) {
        this.userEmails.remove(index);
    }

    public void addEmail(Email email) {
        this.userEmails.add(0, email);
    }

    public ObservableList<Email> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(ObservableList<Email> userEmails) {
        this.userEmails = userEmails;
    }

    public ObservableList<Email> getUserEmailsSnd() { return userEmailsSnd; }

    public void setUserEmailsSnd(ObservableList<Email> userEmailsSnd) { this.userEmailsSnd = userEmailsSnd; }
}
