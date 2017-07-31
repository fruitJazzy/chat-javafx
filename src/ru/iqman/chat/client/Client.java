package ru.iqman.chat.client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import ru.iqman.chat.client.message.Message;
import ru.iqman.chat.client.message.User;

import java.io.*;
import java.net.Socket;


/**
 * Created by fruitjazzy on 28.07.17.
 */
public class Client implements Runnable{

    private ObservableList<Text> textChat;
    private ObservableList<Text> userList;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private final String HOST;
    private final int PORT;
    private final User user;

    public Client(String host,int port, User user, ObservableList<Text> text, ObservableList<Text> users) {
        this.HOST = host;
        this.PORT = port;
        this.user = user;
        this.textChat = text;
        this.userList = users;
    }

    private void connect() throws IOException {
        Message message = new Message();
        message.setUser(user);
        oos.writeObject(message);
    }

    @Override
    public void run() {
        try {
            System.out.printf("starting connection\n");

            socket = new Socket(HOST, PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.printf("connected\n");
        }
        catch (IOException e) {
            System.out.printf("Can't connection to %s port %d\n", HOST, PORT);
        }


        try {

            connect();
            while (socket.isConnected()) {
                Message message = (Message) ois.readObject();

                if (message != null) {
                    switch (message.getMessageType()) {
                        case USER:
                            Platform.runLater(() -> {
                                userList.clear();
                                for (User u : message.getUsers()) {
                                    Text text = new Text(u.getName());
                                    text.setFill(Paint.valueOf(u.getColor()));
                                    userList.add(text);
                                }
                            });
                            break;
                        case MESSAGE:
                            Platform.runLater(() -> {
                                Text text = new Text(message.getMessage());
                                text.setFill(Paint.valueOf(message.getUser().getColor()));
                                textChat.add(text);
                            });
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    private void closeConnection() {
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket.close();
            System.out.printf("connection is close\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}