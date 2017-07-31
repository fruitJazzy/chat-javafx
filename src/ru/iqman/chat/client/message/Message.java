package ru.iqman.chat.client.message;

import java.io.Serializable;
import java.util.Queue;

public class Message implements Serializable{
    private String message;
    private MessageType messageType;
    private User user;
    private Queue<User> users;

    public Queue<User> getUsers() {
        return users;
    }

    public void setUsers(Queue<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (!message.equals(message1.message)) return false;
        if (messageType != message1.messageType) return false;
        return user.equals(message1.user);
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + messageType.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", messageType=" + messageType +
                ", user=" + user +
                '}';
    }
}
