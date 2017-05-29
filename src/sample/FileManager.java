package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

/**
 * Created by esvm on 09/05/17.
 */
public class FileManager implements Initializable {

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

    private final Socket socket;

    public FileManager(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Esta Thread calcula o RTT atual da conexão a cada 500ms

        new Thread(() -> {
            while (true) {
                try {
                    final Socket socket2 = new Socket(Controller.host, 2021);
                    DataOutputStream stream = new DataOutputStream(socket2.getOutputStream());
                    DataInputStream in = new DataInputStream(socket2.getInputStream());
                    //RTT INIT
                    /*
                        Para calcular o RTT estou enviando um pacote pequeno,de 5 bytes
                        Salvo o tempo inicial
                        Após receber um pacote de confirmação do servidor salvo o tempo final
                        A diferença dos dois tempos é o meu RTT
                        Em seguida coloco numa label
                    */
                    byte[] rttMessage = "RTTSE".getBytes();
                    long start = System.currentTimeMillis();
                    stream.write(rttMessage, 0, 5);
                    in.read(rttMessage, 0, 5);
                    long end = System.currentTimeMillis();
                    rtt = Math.abs(start - end);

                    Platform.runLater(() -> lb_rtt.setText("RTT = " + rtt + "ms"));
                    //RTT END
                    socket2.close();
                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    void bt_pickClick(ActionEvent event) {
        //Aqui é a função para o usuário escolher qual arquivo vai enviar
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        file = fileChooser.showOpenDialog(bt_pick.getScene().getWindow());
        if (file != null)
            lb_name.setText(file.getName());

    }

    public static long rtt = 0;

    @FXML
    void bt_sendClick(ActionEvent event) {
        final File file2 = file;

        if (file2 != null) {
            Thread thread = new Thread(() -> {
                try {
                    OutputStream stream = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    if (file2 != null) {
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file2));

                        //RTT INIT
                        /*
                            Aqui eu passo a mensagem de RTTNO indicando que é uma transferência de arquivo
                            e não apenas um cálculo de RTT que é feito na outra Thread
                         */
                        byte[] rttMessage = "RTTNO".getBytes();
                        stream.write(rttMessage, 0, 5);
                        in.read(rttMessage, 0, 5);

                        //RTT END

                        //HEADER INIT
                        /*
                            Aqui é onde eu seto o header com o caminho inicial do arquivo
                            Não está sendo utilizado
                            Um detalhe importante, aqui eu forço o header ter sempre 256 bytes, mesmo que tenha menos
                            Caso necessário, completo com o byte 0
                         */
                        byte[] bytes = new byte[16 * 1024];
                        int count = 0;
                        long current = 0;

                        byte[] header = new byte[256];

                        byte[] path = String.valueOf(file2.length()).getBytes();

                        for (int i = 0; i < path.length; i++) {
                            header[i] = path[i];
                        }

                        for (int i = path.length; i < 256; i++) {
                            header[i] = 0;
                        }
                        stream.write(header, 0, 256);
                        //HEADER END

                        //FILE TRANSFER
                        /*
                            Aqui é onde o arquivo é enviado e a barra de progresso atualizada
                         */
                        while ((count = bis.read(bytes)) > 0) {
                            stream.write(bytes, 0, count);
                            current += count;
                            final long send = current;
                            Platform.runLater(() -> progress.setProgress((send / Math.ceil(file2.length()))));
                        }
                    }

                    Platform.runLater(() -> {
                        lb_name.setText("Transfer Completed!");
                        bt_send.setDisable(true);
                        bt_pick.setDisable(true);
                    });

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();

                    throw new RuntimeException(e);
                }


            });


            thread.setUncaughtExceptionHandler((t, e) -> {
                e.printStackTrace();

                Platform.runLater(() -> {
                    lb_name.setText("Transfer Failed");
                    bt_send.setDisable(true);
                    bt_pick.setDisable(true);
                    progress.setProgress(0);
                });
            });

            thread.start();

        }
    }


}
