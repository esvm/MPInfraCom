package sample;

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

    File file;

    @FXML
    void bt_pickClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        file = fileChooser.showOpenDialog(bt_pick.getScene().getWindow());
        if (file != null)
            lb_name.setText(file.getName());
    }

    @FXML
    void bt_sendClick(ActionEvent event) {
        final File file2 = file;
        if(file2 != null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int port = 2020;
                    String address = "localhost";
                    try {
                        Socket socket = new Socket(address, port);
                        OutputStream stream = socket.getOutputStream();

                        if(file2 != null)
                        {
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file2));

                            byte[] bytes = new byte[16*1024];
                            int count = 0;
                            int current = 0;

                            byte[] header = new byte[64];

                            byte[] path = file2.getPath().getBytes();

                            for (int i = 0; i < path.length; i++) {
                                header[i] = path[i];
                            }

                            for (int i = path.length; i < 64; i++) {
                                header[i] = 0;
                            }


                            stream.write(header, 0, 64);

                            while((count = bis.read(bytes)) > 0)
                            {
                                current += count;
                                progress.setProgress((current / Math.ceil(file2.length())));
                                stream.write(bytes, 0, count);
                            }
                            stream.write("\n".getBytes());
                        }

                        socket.close();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

}
