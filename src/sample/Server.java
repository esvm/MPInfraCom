package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    @Override
    public void run() {
        // TODO Auto-generated method stub
        int port = 2020;

        try {
            ServerSocket tmpsocket = new ServerSocket(port);

            while (true) {
                final Socket socket = tmpsocket.accept();
                //Pra cada arquivo que será enviado, uma nova janela irá abrir.
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverWindow.fxml"));
                fxmlLoader.setController(new serverController(socket));
                final Parent root = fxmlLoader.load();

                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 800, 600));
                    stage.show();
                });
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}


