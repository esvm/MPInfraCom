package sample;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {

	public Client(File file)
	{

	}

	private static File file;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 2020;
		String address = "g4c01";

		try {
			Socket socket = new Socket(address, port);
			BufferedOutputStream stream = new BufferedOutputStream(socket.getOutputStream());
			if(file != null)
			{
				FileInputStream fileInputStream = new FileInputStream(file);


				int size = fileInputStream.available();
				System.out.println("certo: " + size);
				byte[] bytes = new byte[16*1024];
				int count;
				while((count = fileInputStream.read(bytes)) > 0 )
				{
					stream.write(bytes, 0, count);
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


	}

}
