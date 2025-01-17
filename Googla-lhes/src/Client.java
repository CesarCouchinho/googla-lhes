import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

public class Client {

	private static String port = "1337";

	private ObjectOutputStream outToServer;
	private ObjectInputStream inFromServer;
	private Window window;

	/** Connect to Server
	 * 
	 */
	public void connectToServer() {
		Socket clientSocket;
		try {
			InetAddress host = InetAddress.getLocalHost();
			clientSocket = new Socket(host, Integer.parseInt(port));

			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inFromServer = new ObjectInputStream(clientSocket.getInputStream());

			outToServer.writeObject("client");

			window = new Window(this);

			while (true) {
				try {
					Object o = inFromServer.readObject();
					if (o instanceof Map<?, ?>) {
						@SuppressWarnings("unchecked")
						Map<String, Integer> sortedByCountedMap = (Map<String, Integer>) o;
						window.updateWindow(sortedByCountedMap);
						System.out.println("Client received from Server filtered list" + " (size: " + sortedByCountedMap.size() + ")");
					} 
					if (o instanceof String) {
						String yo = (String) o;
						System.out.println(yo);
					}
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
			System.out.println("Client sent to server: " + filter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.connectToServer();
	}

}
