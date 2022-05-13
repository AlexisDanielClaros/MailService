package Server;

import Client.Email;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TasksPool implements Runnable {
    private final ModelServer model;
    private final Socket client;
    private ArrayList<String> username_list;
    private ArrayList<Email> receivedMails;
    private static ArrayList<Email> waitingMails = new ArrayList<>();
    private ArrayList<String> usersLogged = new ArrayList<>();

    TasksPool(Socket client, ModelServer model) throws IOException {
        this.model = model;
        this.client = client;
        username_list = loadUsers();
        //username_list.add("alexis@mail.com");
        //username_list.add("valentina@mail.com");
        //username_list.add("elisabetta@mail.com");
        receivedMails = loadMails();
    }


    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(client.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream())) {
                    String command = (String) in.readObject();
                    String username = (String) in.readObject();
                    switch (command) {
                        case "NewMail":
                            Email newMail = Email.readObject(in);
                            boolean userExist = checkUserExist(newMail.getRecipients());
                            if (userExist) {
                                saveMails(newMail);
                                sendMail(newMail, out, username);
                                Platform.runLater(() -> model.addLog(username + " sended a new email"));
                            } else out.writeObject("userNotFound");
                            break;
                        case "Login":
                            boolean userFound = userLogin(username);
                            if (userFound) {
                                loadUserMails(username, out);
                                checkNewMails(username, out);
                                Platform.runLater(() -> model.addLog("User logged"));
                            } else {
                                Platform.runLater(() -> model.addLog("User not found"));
                                out.writeObject("NotFound");
                            }
                            break;
                        case "Logout":
                            Platform.runLater(() -> model.addLog(username + " logged out"));
                            logout(username);
                            out.writeObject("Bye, " + username + "!");
                            break;
                        case "Delete":
                            Email email = Email.readObject(in);
                            deleteMail(email);
                            Platform.runLater(() -> model.addLog(username + " deleted an email"));
                            out.writeObject("Mail deleted");
                            break;
                        case "Refresh":
                            loadUserMails(username, out);
                            break;
                        case "GetId":
                            out.writeObject(getLastId());
                            break;
                    }
        }
        catch (Exception e) {
        }
    }

    private int getLastId() {
        return receivedMails.get(receivedMails.size()-1).getId();
    }

    private boolean checkUserExist(ArrayList<String> recipients) throws FileNotFoundException {
        ArrayList<String> users = loadUsers();
        for (String recipient : recipients){
            if (!users.contains(recipient)){
                Platform.runLater(() -> model.addLog(recipient + " not found"));
                return false;
            }
        }
        return true;
    }

    synchronized private void loadUserMails(String username, ObjectOutputStream out) throws IOException {
        out.writeObject("Logged");
        ArrayList<Email> userEmails = new ArrayList<>();
        ArrayList<Email> userEmailsSnd = new ArrayList<>();
        for (Email mail : receivedMails){
            if (mail.getRecipients().contains(username))
                userEmails.add(mail);
            else if (mail.getSender().equals(username))
                userEmailsSnd.add(mail);
        }
        out.writeObject(userEmails.size());
        for (Email email : userEmails){
            Email.writeObject(out, email);
        }
        out.writeObject(userEmailsSnd.size());
        for (Email email : userEmailsSnd){
            Email.writeObject(out, email);
        }
    }

    synchronized private void checkNewMails(String username, ObjectOutputStream out) throws IOException {
        boolean newMails = false;
        ArrayList<Integer> mailsTosend = new ArrayList<>();
        for (Email email : waitingMails){
            if (email.getRecipients().contains(username)) {
                mailsTosend.add(waitingMails.indexOf(email));
                newMails = true;
            }
        }
        out.writeObject(newMails);
        if (newMails) {
            out.writeObject(mailsTosend.size());
            for (int i : mailsTosend){
                Email.writeObject(out, waitingMails.get(i));
                waitingMails.remove(i);
            }
        }
    }

    private void logout(String username) {
        usersLogged.remove(username);
    }

    private boolean userLogin(String username) throws IOException {
        Platform.runLater(() -> model.addLog("Successfully connected with:  " + username));
        for (String a : username_list) {
            if (a.equals(username)) {
                usersLogged.add(username);
                return true;
            }
        }
        return false;
    }

    synchronized private void saveMails(Email newMail){
        try {
            receivedMails.add(newMail);
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/userEmails.txt", true));
            StringBuilder recipients = new StringBuilder();
            for (String recipient : newMail.getRecipients()) {
                recipients.append(recipient).append("; ");
            }
            String s = newMail.getId() + "; " + newMail.getSender() + "; " + newMail.getArgument() + "; " + newMail.getMessage() + "; " + newMail.getDate() + "; " + recipients;
            writer.append(s);
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized private void sendMail(Email new_email, ObjectOutputStream out, String username) throws IOException, ClassNotFoundException {
        boolean found = false;
        for (String recipient : new_email.getRecipients()){
            if (username.equals(recipient)) {
                found = true;
                out.writeObject("Mail");
                Email.writeObject(out, new_email);
            }
        }
        if (!found){
            out.writeObject("NoMail");
            waitingMails.add(new_email);
        }
    }

    synchronized private ArrayList<Email> loadMails() throws FileNotFoundException {
        Scanner s = new Scanner(new File("src/main/resources/userEmails.txt"));
        Scanner scanLine;
        ArrayList<Email> list = new ArrayList<>();
        while(s.hasNextLine()) {
            ArrayList<String> recipients = new ArrayList<>();
            Email temp = new Email();
            String currentline = s.nextLine();
            scanLine = new Scanner(currentline).useDelimiter("\\s*[;]\\s*");
            temp.setId(Integer.parseInt(scanLine.next()));
            temp.setSender(scanLine.next());
            temp.setArgument(scanLine.next());
            temp.setMessage(scanLine.next());
            temp.setDate(scanLine.next());
            while (scanLine.hasNext()) {
                recipients.add(scanLine.next());
            }
            temp.setRecipients(recipients);
            list.add(temp);
            scanLine.close();
        }
        //System.out.println(list.get(0).getId() +"  "+ list.get(0).getArgument() +"  "+ list.get(0).getMessage() +"  "+ list.get(0).getRecipients());
        //System.out.println(list.get(1).getId() +"  "+ list.get(1).getArgument() +"  "+ list.get(1).getMessage() +"  "+ list.get(1).getRecipients());
        s.close();
        return list;
    }

    synchronized private void deleteMail(Email email) throws IOException {
        int id = email.getId();
        receivedMails.removeIf(m -> m.getId() == id);
        File inputFile = new File("src/main/resources/userEmails.txt");
        File tempFile = new File("src/main/resources/temp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        Scanner scanLine;
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            scanLine = new Scanner(currentLine);
            if(scanLine.next().contains(String.valueOf(id))) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
            scanLine.close();
        }
        writer.close();
        reader.close();
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    synchronized private ArrayList<String> loadUsers() throws FileNotFoundException {
        Scanner s = new Scanner(new File("src/main/resources/usersList.txt"));
        ArrayList<String> list = new ArrayList<>();
        while(s.hasNextLine()) {
            list.add(s.nextLine());
        }
        s.close();
        return list;
    }
}


/*
- email divise tra inviate e ricevute
- cancellare solo una email
- se cancello, cancello solo quella dell'utente

 */