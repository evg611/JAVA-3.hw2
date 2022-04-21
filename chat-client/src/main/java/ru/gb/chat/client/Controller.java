package ru.gb.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField, loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, msgPanel;

    @FXML
    ListView<String> clientsList;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private History history;

    public void setUsername(String username) {//установить
        //имя пользователя
        this.username = username;
        boolean usernameIsNull = username == null;
        loginPanel.setVisible(usernameIsNull);
        loginPanel.setManaged(usernameIsNull);
        msgPanel.setVisible(!usernameIsNull);
        msgPanel.setManaged(!usernameIsNull);
        clientsList.setVisible(!usernameIsNull);
        clientsList.setManaged(!usernameIsNull);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUsername(null);
        history = new History();
    }

    public void login() {
        if (loginField.getText().isEmpty()) {
            showErrorAlert("Имя пользователя не может быть пустым");
            return;
        }

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/login " + loginField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread(() -> {
                try {
                    // Цикл авторизации
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")) {
                            //и , что после успешного логина

                            setUsername(msg.split("\\s")[2]);
                            history.init(msg.split("\\s")[1]);//history проинициализируйся нашим логином первым
                            //когда мы зашли, мы history проинициализировали,
                            msgArea.clear();
                            //мы область сообщений чистим
                            msgArea.appendText(history.load());
                            // В msgArea добавляем что history загрузит(выдаст на load)
                            break;
                        }
                        if (msg.startsWith("/login_failed ")) {
                            String cause = msg.split("\\s", 2)[1];
                            msgArea.appendText(cause + "\n");
                        }
                    }
                    // Цикл общения
                    while (true) {
                        String msg = in.readUTF();

                        // todo вынести этот блок
                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/clients_list ")) {
                                // /clients_list Bob Max Jack
                                String[] tokens = msg.split("\\s");
                                Platform.runLater(() -> {
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);
                                    }
                                });
                            }
                            continue;
                        }
                        history.write(msg + "\n");//msg + пробел
                        msgArea.appendText(msg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            t.start();
        } catch (IOException e) {
            showErrorAlert("Невозможно подключиться к серверу");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            showErrorAlert("Невозможно отправить сообщение");
        }
    }

    private void disconnect() {
        setUsername(null);
        history.close();//history делайся close
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("March Chat FX");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
