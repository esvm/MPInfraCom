package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class Server implements Runnable {

    int port = 2020;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub


        try {
            ServerSocket tmpsocket = new ServerSocket(port);

            while (true) {
                final Socket socket = tmpsocket.accept();

                InputStream in = socket.getInputStream();
                //RTT INIT
                byte[] rttMessage = new byte[5];
                in.read(rttMessage, 0, 5);
                StringBuilder rtt = new StringBuilder();
                for (int i = 0; i < 5; i++) {
                    rtt.append(new String(new byte[]{rttMessage[i]}));
                }
                String mes = rtt.toString();
                OutputStream rttOutput = socket.getOutputStream();
                rttMessage = "RTTOK".getBytes();
                rttOutput.write(rttMessage, 0, 5);
                //RTT END
                if (mes.equals("RTTNO")) {
                    //Pra cada arquivo que será enviado, uma nova janela irá abrir.
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverWindow.fxml"));
                    fxmlLoader.setController(new serverController(socket));
                    final Parent root = fxmlLoader.load();

                    Platform.runLater(() -> {
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root, 800, 600));
                        stage.show();


                        stage.setOnCloseRequest(event -> {
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
                    });
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}


