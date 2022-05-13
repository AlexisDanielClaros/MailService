package Client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Network {
    private Socket socket;
    private final int PORT = 8189;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Network(){
        socket = null;
        out = null;
        in = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            socket = new Socket(localHost, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            ThreadServerConnection.setLogged(false);
        }
    }

    public Boolean userLogin(String username){
        boolean found = false;
        try {
            if (this.out != null) {
                out.writeObject("Login");
                out.writeObject(username);
                out.flush();
                String answer = (String) in.readObject();
                if (answer.equals("Logged")){
                    Main.setCurrentUser(new User(username));
                    loadUserEmails(in);
                    boolean newMails = (Boolean) in.readObject();
                    if (newMails) loadNewMails(in); // there are new mails for the user
                    else sendNotify("Welcome, " + username + "!");
                    found = true;
                }
                else if (answer.equals("NotFound")) ErrorController.generateError("User not found");
            }

        } catch (IOException | ClassNotFoundException e) {
            serverError();
        } finally {
            closeConnection();
        }
        return found;
    }

    private void loadNewMails(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Email email;
        int index = (int) in.readObject();
        for (int i = 0; i < index; i++) {
            email = Email.readObject(in);
            Controller.addToListView(email);
        }
        sendNotify("Welcome, you have new mails!");
    }

    private boolean loadUserEmails(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObservableList<Email> userEmails = FXCollections.observableArrayList();
        ObservableList<Email> userEmailsSnd = FXCollections.observableArrayList();
        int index = (int) in.readObject();
        for(int i = 0; i<index; i++){
            userEmails.add(Email.readObject(in));
        }
        int index2 = (int) in.readObject();
        for(int i = 0; i<index2; i++){
            userEmailsSnd.add(Email.readObject(in));
        }
        User user = Main.getCurrentUser();
        if (user != null && (user.getUserEmails().size() != index || user.getUserEmailsSnd().size() != index2)) { //Check if the are changes to the arrays
            userEmails.sort(Collections.reverseOrder(Comparator.comparing(Email::getDate)));
            userEmailsSnd.sort(Collections.reverseOrder(Comparator.comparing(Email::getDate)));
            Main.getCurrentUser().setUserEmails(userEmails);
            Main.getCurrentUser().setUserEmailsSnd(userEmailsSnd);
            return true;
        }
        return false;
    }

    public void sendNewEmail(Email email)  {
        try {
            if (this.out != null) {
                out.writeObject("NewMail");
                out.writeObject(Main.getCurrentUser().getName());
                Email.writeObject(out,email);
                out.flush();
                String answer = (String) in.readObject();
                if (answer.equals("NoMail")) sendNotify("Message sent");
                else if (answer.equals("Mail")) {
                    Email receivedMail = Email.readObject(in);
                    Main.getCurrentUser().getUserEmails().add(0, receivedMail);
                    sendNotify("You have new mail");
                }
                else if (answer.equals("userNotFound")) {
                    Platform.runLater(() -> {
                        try {
                            ErrorController.generateError("Email not found");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
            else serverError();
        } catch (IOException | ClassNotFoundException e) {
            serverError();
        } finally {
            closeConnection();
        }
    }

    public void logout(String username) {
        try{
            if (this.out != null) {
                out.writeObject("Logout");
                out.writeObject(username);
                out.flush();
                String answer = (String) in.readObject();
                sendNotify(answer);
            }
            else serverError();
        } catch (Exception e) {
            serverError();
        }finally {
            closeConnection();
        }
    }

    public boolean deleteEmail(Email email) {
        boolean success = false;
        try{
            if (this.out != null) {
                out.writeObject("Delete");
                out.writeObject(Main.getCurrentUser().getName());
                Email.writeObject(out, email);
                out.flush();
                String answer = (String) in.readObject();
                sendNotify(answer);
                success = true;
            }
            else serverError();

        } catch (Exception e) {
            serverError();
        }finally { closeConnection(); }
        return success;
    }

    public boolean refreshList(){
        try {
            if (this.out != null) {
                out.writeObject("Refresh");
                out.writeObject(Main.getCurrentUser().getName());
                out.flush();
                String answer = (String) in.readObject();
                if (answer.equals("Logged")){
                    boolean changes = loadUserEmails(in);
                    if (changes) return true;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
        } finally {
            closeConnection();
        }
        return false;
    }

    public int getLastId(){
        try {
            if (this.out != null) {
                out.writeObject("GetId");
                out.writeObject("Null"); //don't need a user
                out.flush();
                int id = (Integer) in.readObject();
                return id;
            }

        } catch (IOException | ClassNotFoundException e) {
            serverError();
        } finally {
            closeConnection();
        }
        return -1;
    }

    public Socket getSocket(){ return this.socket; }

    private void sendNotify(String message) {
        Platform.runLater(() -> {
            try {
                NotifyController.generateNotify(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void serverError() {
        Platform.runLater(() -> {
            try {
                ErrorController.generateError("Server's currently offline");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void closeConnection() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
