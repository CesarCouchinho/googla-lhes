import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

	private static String port = "1337";

	ObjectOutputStream outToServer;
	ObjectInputStream inFromServer;
	Window window;

	public void connectToServer() {
		try {
			
			InetAddress host = InetAddress.getLocalHost();
			Socket clientSocket = new Socket(host, Integer.parseInt(port));
			
			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inFromServer = new ObjectInputStream(clientSocket.getInputStream());
			
			window = new Window(this);
			sendFilterToServer("");

			while(true) {
				
				try {
					List<String> filteredList = (List<String>) inFromServer.readObject();
					
					System.out.println("Client received from Server filtered list" + " (size: " +filteredList.size()+ ")");
					
					window.updateWindow(filteredList);
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFilterToServer(String filter) {
		try {
			outToServer.writeObject(filter);
			System.out.println("Client sent to server: " +filter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.connectToServer();
	}

}
