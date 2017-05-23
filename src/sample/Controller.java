package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
        Parent root = FXMLLoader.load(getClass().getResource("fileManager.fxml"));
        Stage stage = new Stage();
        stage.setTitle("File Transfer");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();

    }
}
