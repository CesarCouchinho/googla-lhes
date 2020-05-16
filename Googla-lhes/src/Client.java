import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

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
			
//			outToServer.writeObject("client");
			
			window = new Window(this);

			while(true) {
				try {
					Map <String,Integer> sortedByCountedMap = (Map <String,Integer>) inFromServer.readObject();
					
					System.out.println("Client received from Server filtered list" + " (size: " +sortedByCountedMap.size()+ ")");
					
					window.updateWindow(sortedByCountedMap);
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/** Sends the filter to server through object stream 
	 * 
	 * @param filter keyword to send to server
	 */
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
