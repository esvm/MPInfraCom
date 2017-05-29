package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
            Esta é a classe principal
            Aqui é onde o Javafx verifica qual a primeira janela que será aberta no projeto
            Cada janela é composta de um FXML e um Controller
            Este controller pode estar linkado com o FXML direto no Design ou então via código como é o caso do serverController
         */
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("File Transfer");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event ->
                Platform.exit()
        );
        /*
            Esta é a Thread do servidor.
            Foi criada no Main porque este só é executado na primeira vez que o programa é aberto
            Só é necessário um servidor, mas são possíveis vários clientes
            Para inicializar novos clientes, basta clicar várias vezes no OK na tela inicial
         */
        new Thread(new Server(2020)).start(); //Transfer
        new Thread(new Server(2021)).start(); //RTT
    }


    public static void main(String[] args) {
        launch(args);
    }
}
