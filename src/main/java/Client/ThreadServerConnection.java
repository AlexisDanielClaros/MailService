package Client;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ThreadServerConnection extends Thread {
    private Network net;
    private String currentUser;
    private Label serverStatus;
    private ListView<Email> listView;
    private static boolean  logged = true;
    private boolean running = true;

    public ThreadServerConnection(String currentUser, Label serverStatus, ListView<Email> listView){
        this.currentUser = currentUser;
        this.serverStatus = serverStatus;
        this.listView = listView;
    }

    @Override
    public void run() {
        while (running) {
            try {
                net = new Network();
                if (!logged) {
                    boolean reconnecting = net.userLogin(currentUser);
                    if (reconnecting) {
                        Platform.runLater(() -> serverStatus.setText("online"));
                        Platform.runLater(() -> serverStatus.setStyle("-fx-text-fill: #09f218"));
                        logged = true;
                    }
                    else{
                        logged = false;
                        Platform.runLater(() -> serverStatus.setText("offline"));
                        Platform.runLater(() -> serverStatus.setStyle("-fx-text-fill: red"));
                    }
                }
                else {
                    boolean changes = net.refreshList();
                    if (changes) {
                        Platform.runLater(() -> listView.getItems().clear());
                        Platform.runLater(() -> listView.getItems().setAll(Main.getCurrentUser().getUserEmails()));
                    }
                }

            } catch (Exception e) {
                logged = false;
                Platform.runLater(() -> serverStatus.setText("offline"));
                Platform.runLater(() -> serverStatus.setStyle("-fx-text-fill: red"));
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeThread(){
        this.running = false;
    }

    public static void setLogged(boolean log){ logged = log;}
}

