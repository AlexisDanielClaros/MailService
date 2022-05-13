package Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class ModelServer implements Serializable {
    private ObservableList<String> logs;

    public ModelServer(){
        logs = FXCollections.observableArrayList();
    }

    public ObservableList<String> getLogs() {
        return logs;
    }

    public void addLog(String log){
        logs.add(log);
    }

}
