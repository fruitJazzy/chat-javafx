package ru.iqman.chat.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.iqman.chat.client.message.Message;
import ru.iqman.chat.client.message.MessageType;
import ru.iqman.chat.client.message.User;


/**
 * Created by fruitjazzy on 27.07.17.
 */
public class MainApp extends Application {
    private static final int HEIGHT = 500;
    private static final int WIDTH = 900;
    private static final int WIDTH_USER_LIST = 200;
    public static final String TITLE = "Chategram";

    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private User user;

    private Client client;

    private Stage primaryStage;
    private Stage chatStage;


    // UI for login page
    private Text textLogin;
    private TextField textFieldLogin;
    private ColorPicker colorPickerLogin;
    private Button buttonLogin;

    // UI for chat page
    private Button sendButton;
    private ObservableList<Text> textChat;
    private ObservableList<Text> userList;
    private ListView<Text> chatListView;
    private ListView<Text> userListView;
    private TextField inputField;

    public void initMainPage() {
        textFieldLogin = new TextField();
        textLogin = new Text();
        textLogin.setText("ИМЯ:  ");
        textLogin.setX(400);
        textLogin.setY(200);

        colorPickerLogin = new ColorPicker();

        buttonLogin = new Button("войти");
        buttonLogin.setLayoutX(30);
        buttonLogin.setLayoutY(30);
        buttonLogin.setOnAction(event -> {
            initChatPage();
            System.out.println("try to connected");
            String name = textFieldLogin.getCharacters().toString();
            String style = colorPickerLogin.getValue().toString();
            user = new User();
            user.setName(name);
            user.setColor(style);
            System.out.println("user name: " + name + ", color: " + style);

            client = new Client(HOST, PORT, user, textChat, userList);
            new Thread(client).start();
            chatStage.show();
            primaryStage.close();
        });
    }

    public void initChatPage() {
        textChat = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();

        chatListView = new ListView<>(textChat);
        userListView = new ListView<>(userList);

        // todo поменять цвет listview
        userListView.setStyle(" -fx-background-color: #4087f9");

        chatListView.setMaxHeight(HEIGHT - 100);
        chatListView.setMaxWidth(WIDTH - WIDTH_USER_LIST);

        userListView.setMaxHeight(HEIGHT);
        userListView.setMaxWidth(WIDTH_USER_LIST);

        userListView.setItems(userList);

        sendButton = new Button("Отправить");

        // горизонтально располагаем инпут поле и кнопку
        // todo поменять размеры
        inputField = new TextField();

        inputField.setMaxWidth(300);
        inputField.setMaxHeight(50);
        HBox box = new HBox(inputField, sendButton);
        // вертикально добавляем поле с тестом чата и поле ввода текста
        VBox vBox = new VBox(chatListView, box);

        BorderPane pane = new BorderPane();
        pane.setLeft(userListView);
        pane.setCenter(vBox);

        sendButton.setOnAction(event -> {
            sendMessage(inputField);
        });

        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        chatStage = new Stage();
        chatStage.setTitle(TITLE);
        chatStage.setScene(scene);
    }

    private void sendMessage(TextField inputField) {
        String text = inputField.getCharacters().toString();

        Message message = new Message();
        message.setUser(user);
        message.setMessage(text);
        message.setMessageType(MessageType.MESSAGE);

        client.send(message);
        inputField.clear();
    }

    @Override
    public void start(Stage stage) throws Exception {
        initMainPage();

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(textLogin, 0, 0);
        gridPane.add(textFieldLogin, 1, 0);
        gridPane.add(colorPickerLogin, 1, 1);
        gridPane.add(buttonLogin, 2, 2);

        Scene scene = new Scene(gridPane, WIDTH, HEIGHT);

        primaryStage = new Stage();
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}