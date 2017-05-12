package sample;

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
            InputStream in;
            while (true) {
                final Socket socket = tmpsocket.accept();

                in = socket.getInputStream();
                //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                byte[] pathby = new byte[64];
                in.read(pathby, 0, 64);
                StringBuilder tex = new StringBuilder();
                for (int i = 0; i < 64; i++) {
                    if(pathby[i] != 0)
                        tex.append(new String(new byte[] {pathby[i]}));
                    else
                        break;
                }
                String path = tex.toString(); //caminho com nome do arquivo

                tex = new StringBuilder();
                String[] split = path.split("/");
                split[split.length - 2] = "Documents";
                for (int i = 0; i < split.length; i++) {
                    if(i != split.length - 1)
                        tex.append(split[i] + "/");
                    else
                        tex.append(split[i]);
                }

                String newPath = tex.toString();
                OutputStream out = new FileOutputStream(newPath);

                int count;
                byte[] bytes = new byte[1024];
                while ((count = in.read(bytes, 0, bytes.length)) > 0) {
                    out.write(bytes, 0, count);
                }

                out.close();
                in.close();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}


