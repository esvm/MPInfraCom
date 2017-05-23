package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by esvm on 09/05/17.
 */
public class FileManager {

    @FXML
    private Button bt_send;

    @FXML
    private Button bt_pick;

    @FXML
    private Label lb_name;

    @FXML
    private ProgressBar progress;

    @FXML
    private Label lb_rtt;

    public static File file;

    String address = "localhost";

    @FXML
    void bt_pickClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        file = fileChooser.showOpenDialog(bt_pick.getScene().getWindow());
        if (file != null)
            lb_name.setText(file.getName());

        lb_rtt.setText(rtt + "");
    }

    public static long rtt = 0;

    @FXML
    void bt_sendClick(ActionEvent event) {
        final File file2 = file;

        if (file2 != null) {
            new Thread(() -> {
                try {
                    final Socket socket = new Socket(Controller.host, Controller.port);
                    OutputStream stream = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    if (file2 != null) {
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file2));

                        //RTT INIT
                        byte[] rttMessage = "RTTSE\n".getBytes();
                        long start = System.currentTimeMillis();
                        stream.write(rttMessage, 0, 6);
                        in.read(rttMessage, 0, 6);
                        long end = System.currentTimeMillis();
                        rtt = Math.abs(start - end);
                        Platform.runLater(() -> lb_rtt.setText(rtt + "") );
                        //RTT END


                        //HEADER INIT
                        byte[] bytes = new byte[16 * 1024];
                        int count = 0;
                        long current = 0;

                        byte[] header = new byte[256];

                        byte[] path = file2.getPath().getBytes();

                        for (int i = 0; i < path.length; i++) {
                            header[i] = path[i];
                        }

                        for (int i = path.length; i < 256; i++) {
                            header[i] = 0;
                        }
                        stream.write(header, 0, 256);
                        //HEADER END

                        //FILE TRANSFER
                        while ((count = bis.read(bytes)) > 0) {
                            stream.write(bytes, 0, count);
                            current += count;
                            final long send = current;
                            Platform.runLater(() ->progress.setProgress((send / Math.ceil(file2.length()))));
                        }

                    }

                    socket.close();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }).start();

        }
    }

}
