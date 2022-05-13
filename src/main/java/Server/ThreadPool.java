package Server;

import Client.Email;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool extends Thread implements Runnable{
    private final ModelServer model;
    private ExecutorService executor;
    private ServerSocket serverSocket;

    public ThreadPool(ModelServer model) {
        this.model = model;
    }

    @Override
    public void run() {
        try  {
            executor = Executors.newFixedThreadPool(5);
            serverSocket = new ServerSocket(8189);
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    executor.execute(new TasksPool(client, model));
                }
                catch (IOException e) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void terminate() throws IOException {
        this.executor.shutdown();
        serverSocket.close();
    }
}
