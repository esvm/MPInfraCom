package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class Controller {

    public static int port;
    public static String host;


    @FXML
    private Button bt_ok;

    @FXML
    private TextField tf_host;

    @FXML
    private TextField tf_port;



    @FXML
    void bt_okClick(ActionEvent event) throws IOException {
        port = Integer.parseInt(tf_port.getText());
        host = tf_host.getText();
        /*
            Assim que o usuário clica no OK na tela inicial, eu abro a janela de transferência de arquivo
            onde o usuário vai escolher o arquivo a ser enviado
         */
        final Socket socket = new Socket(host, port);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fileManager.fxml"));
        fxmlLoader.setController(new FileManager(socket));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("File Transfer");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();

        stage.setOnCloseRequest(event1 -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are you sure?");
            Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            exitButton.setText("OK");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
