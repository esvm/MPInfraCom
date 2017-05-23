package sample;

import com.sun.javafx.binding.StringFormatter;
import com.sun.javafx.runtime.SystemProperties;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by caesa on 22/05/2017.
 */
public class serverController implements Initializable {

    private Parent root;

    @FXML
    private ProgressBar progress;

    @FXML
    private Label lb_time;


    private final Socket socket;

    public serverController(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*Esta Thread é para que a janela não abra após a finalização do Download
          Resumindo, coisa do Javafx. Sem ela, tudo que está aqui iria rodar e só depois a janela
           com a progress bar e tals iria abrir.
         */
        new Thread(() -> {
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
                /*
                    Criei um header com o caminho inicial do arquivo, mas acabei que não utilizei para nada.
                    Poderia colocar para pegar o tamanho do arquivo, por exemplo.
                    Enfim, é um Header de 256 caracteres. Para o que estava utilizando, é o suficiente.
                 */
                byte[] pathby = new byte[256];
                in.read(pathby, 0, 256);
                StringBuilder tex = new StringBuilder();
                for (int i = 0; i < 256; i++) {
                    if (pathby[i] != 0)
                        tex.append(new String(new byte[]{pathby[i]}));
                    else
                        break;
                }
                String path = tex.toString(); //caminho com nome do arquivo
                //HEADER END


                //FILE RECEIVER
                /* Escolher onde o usuário quer salvar o arquivo
                   O while é pra garantir que o usuário já escolheu o local e nome do arquivo que será salvo
                 */
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.setTitle("Save");
                final File[] file = new File[1];
                Platform.runLater(() ->  file[0] = fileChooser.showSaveDialog(progress.getScene().getWindow()));

                while(file[0] == null){
                    Thread.sleep(500);
                }

                FileOutputStream out = new FileOutputStream(file[0]); //Vai escrever o arquivo no diretório e nome especificado pelo usuário

                long current = 0; //quantos bytes já foram recebidos
                final long available = FileManager.file.length(); //Tamanho total do arquivo
                int count;
                byte[] bytes = new byte[16 * 1024];
                int interval = 0;
                long start = System.nanoTime(); //tempo inicial da transferência
                while ((count = in.read(bytes, 0, bytes.length)) > 0) {
                    current += count;
                    out.write(bytes, 0, count);
                    final long received = current;
                    long elapsedTime = System.nanoTime() - start; //Tempo que se passou desde o início da transferência
                    /*A velocidade é dada pela quantidade de bytes já escritos e o tempo que se passou
                      O tempo está em nanossegundos, portanto multiplica por 10^-9 para converter para segundos
                     */
                    double speed = received / (elapsedTime * Math.pow(10, -9));
                    //seta o progresso atual do download
                    Platform.runLater(() ->
                            progress.setProgress(received / Math.ceil(available))
                    );
                    /*A cada 512 para não ficar escrevendo o tempo restante sempre
                      Tinha setado 1024, mas achei que ficou muito
                      O tempo restante é a quantidade de bytes que restam a ser escritos sobre a velocidade
                     */

                    if (interval % 512 == 0)
                        Platform.runLater(() ->
                                lb_time.setText(String.format("%.2fs", (available - received) / speed))
                        );
                    interval++;
                }
                //Depois que terminou a transferência, o tempo de Download é 0s.
                Platform.runLater(() ->
                        lb_time.setText(String.format("0s"))
                );
                in.close();
                out.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }).start();
    }
}
