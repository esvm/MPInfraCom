package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream.GetField;
import java.net.ServerSocket;
import java.net.Socket;


public class Server implements Runnable {

    @Override
    public void run() {
        // TODO Auto-generated method stub
        int port = 2020;

        try {
            ServerSocket tmpsocket = new ServerSocket(port);
            InputStream in;
            while (true) {
                final Socket socket = tmpsocket.accept();

                in = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String ext = br.readLine();
                OutputStream out = new FileOutputStream("/home/CIN/esvm/Desktop/teste" + ext);

                int count;

                byte[] bytes = new byte[1024];
                while ((count = in.read(bytes, 0, bytes.length)) > 0) {
                    out.write(bytes, 0, count);
                }

            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}


