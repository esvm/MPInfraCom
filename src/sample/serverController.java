package sample;
import com.sun.javafx.binding.StringFormatter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by caesa on 22/05/2017.
 */
public class serverController implements Initializable {

    @FXML
    private ProgressBar progress;

    @FXML
    private Label lb_time;


    private final Socket socket;
    public serverController(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            InputStream in = this.socket.getInputStream();

            //RTT INIT
            byte[] rttMessage = new byte[5];
            in.read(rttMessage, 0, 5);
            OutputStream rttOutput = this.socket.getOutputStream();
            rttMessage = "RTTOK".getBytes();
            rttOutput.write(rttMessage, 0, 5);
            //RTT END

            //HEADER INIT
            byte[] pathby = new byte[256];
            in.read(pathby, 0, 256);
            StringBuilder tex = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                if(pathby[i] != 0)
                    tex.append(new String(new byte[] {pathby[i]}));
                else
                    break;
            }
            String path = tex.toString(); //caminho com nome do arquivo
            //HEADER END

                    /*tex = new StringBuilder();
                    String[] split = path.split("/");

                    for (int i = 0; i < split.length; i++) {
                        if(i != split.length - 1)
                            tex.append(split[i] + "/");
                        else
                            tex.append(split[i]);
                    }

                    String newPath = tex.toString();*/
            //FILE RECEIVER
            long start = System.nanoTime();

            OutputStream out = new FileOutputStream("C:\\Users\\caesa\\Documents\\teste" );

            long current = 0;
            final long available = FileManager.file.length();
            int count;
            byte[] bytes = new byte[16*1024];
            int interval = 0;
            while ((count = in.read(bytes, 0, bytes.length)) > 0) {
                current += count;
                out.write(bytes, 0, count);
                final long received = current;
                long elapsedTime = System.nanoTime() - start;
                double speed = received / (elapsedTime * Math.pow(10,-9));
                Platform.runLater(() ->
                        progress.setProgress(received / available)
                );
                if(interval % 1024 == 0)
                    Platform.runLater(() ->
                        lb_time.setText(String.format("%.2fs" ,(available - received) / speed))
                    );
                interval++;
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
