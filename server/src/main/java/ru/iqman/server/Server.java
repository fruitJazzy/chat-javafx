package ru.iqman.server;

import ru.iqman.chat.client.message.Message;
import ru.iqman.chat.client.message.MessageType;
import ru.iqman.chat.client.message.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static final int PORT = 9999;
    private static final Queue<User> users = new ConcurrentLinkedQueue<>();
    private static final Queue<ObjectOutputStream> writers = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("server started");


        try {
            while (true) {
                new Thread(new Worker(serverSocket.accept())).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            serverSocket.close();
        }
    }

    static class Worker implements Runnable {
        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        Worker(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                Message connectMessage = (Message) ois.readObject();
                System.out.println("get message " + connectMessage);
                users.add(connectMessage.getUser());

                writers.add(oos);
                sendNotifications(connectMessage);
                System.out.println("notifications send");

                System.out.println("added writer to writers");
                while (socket.isConnected()) {
                    Message message = (Message) ois.readObject();

                    if (message != null) {
                        switch (message.getMessageType()) {
                            case MESSAGE:
                                write(message);
                                System.out.println("send message from " + message.getUser() + ", text: " + message.getMessage());
                                break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void sendNotifications(Message message) throws IOException {
            message.setMessageType(MessageType.USER);
            message.setUsers(users);
            message.setMessage("присоеденился к чату " + message.getUser().getName());
            System.out.println("users size:" + users.size());
            write(message);
        }

        private void write(Message message) {
            for (ObjectOutputStream writer : writers) {
                try {
                    writer.writeObject(message);
                    writer.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                    writers.remove(writer);
                }
            }
        }

        private void closeConnection() {
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
