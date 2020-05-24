import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DealWithClient extends Thread{

	private Server server;
	private ObjectInputStream inFromClient;
	public ObjectOutputStream outToClient;
	private int clientID;

	public DealWithClient(Server server, Socket socket, ObjectInputStream inFromClient, ObjectOutputStream outToClient, int clientID) {
		System.out.println("DealWithClient thread created");
		this.server = server;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.clientID=clientID;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String filter = (String) inFromClient.readObject();
				System.out.println("Server received from client " + clientID + ": " + filter);

				server.createTasks(filter, clientID);

				while (server.tasksCompleted != server.getNumberOfFiles()) {
					System.out.print("");
				}
				server.sendSortedMapToClient();
				outToClient.reset();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Connection ended by Client");
		}

	}
}
