package Client;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Email implements Serializable {
    private final IntegerProperty id= new SimpleIntegerProperty();
    private final StringProperty sender= new SimpleStringProperty();
    private final StringProperty argument= new SimpleStringProperty();
    private final StringProperty message= new SimpleStringProperty();
    private final StringProperty date= new SimpleStringProperty();
    private ArrayList<String> recipients;;

    public Email(String sender, String argument, String message, String date, ArrayList<String> recipients){
        Network net = new Network();
        this.id.set(net.getLastId()+1);
        this.argument.setValue(argument);
        this.sender.setValue(sender);
        this.message.setValue(message);
        this.date.setValue(date);
        this.recipients = recipients;
    }

    public Email() {
        recipients = new ArrayList<>();
    }

    public Email(int id, String sender, String argument, String message, String date, ArrayList<String> recipients) {
        this.id.set(id);
        this.argument.setValue(argument);
        this.sender.setValue(sender);
        this.message.setValue(message);
        this.date.setValue(date);
        this.recipients = recipients;
    }

    public String getSender() {
        return sender.getValue();
    }

    public void setSender(String sender) {
        this.sender.setValue(sender);
    }

    public String getArgument() {
        return argument.getValue();
    }

    public void setArgument(String argument) {
        this.argument.setValue(argument);
    }

    public String getMessage() {
        return message.getValue();
    }

    public void setMessage(String text) {
        this.message.setValue(text);
    }

    public int getId() { return this.id.getValue(); }

    public void setId(int id) { this.id.setValue(id); }

    public ArrayList<String> getRecipients() { return recipients; }

    public void setRecipients(ArrayList<String> recipients) { this.recipients = recipients ; }

    public String getDate() { return date.get(); }

    public void setDate(String date) { this.date.setValue(date); }

    public static Email readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        SimpleIntegerProperty id = new SimpleIntegerProperty((int) in.readObject());
        SimpleStringProperty sender= new SimpleStringProperty((String) in.readObject());
        ArrayList<String>  recipients = readRecipients(in);
        SimpleStringProperty argument= new SimpleStringProperty((String) in.readObject());
        SimpleStringProperty message= new SimpleStringProperty((String) in.readObject());
        SimpleStringProperty date = new SimpleStringProperty((String) in.readObject());
        return new Email(id.get(), sender.get(), argument.get(), message.get(), date.get(), recipients);
    }

    private static ArrayList<String> readRecipients(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ArrayList<String> recipients = new ArrayList<>();
        int loop = in.readInt();
        for (int i = 0; i<loop; i++){
            recipients.add((String) in.readObject());
        }
        return recipients;
    }

    public static void writeObject(ObjectOutputStream out, Email email) throws IOException {
        out.writeObject(email.getId());
        out.writeObject(email.getSender());
        writeRecipients(out,email.getRecipients());
        out.writeObject(email.getArgument());
        out.writeObject(email.getMessage());
        out.writeObject(email.getDate());
    }

    private static void writeRecipients(ObjectOutputStream out, ArrayList<String> recipients) throws IOException {
        out.writeInt(recipients.size());
        for(String recipient : recipients)
            out.writeObject(recipient);
    }

}
