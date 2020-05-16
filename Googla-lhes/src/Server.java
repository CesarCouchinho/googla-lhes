import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private static String port = "1337";
	private ServerSocket serverSocket;
	final static File folder = new File("src/news/");
	private final static List<String> filesContentsList = new ArrayList<>(10);
	ObjectInputStream in;
	ObjectOutputStream out;
	DealWithClient[] dealWithClientThreads = new DealWithClient[100];
	int clientID;

	public Server() {
		addFilesFromFolderToList(folder);
		clientID = 0;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized List<String> getFilesContentsList() {
		return filesContentsList;
	}

	public void broker() {
		System.out.println("Waiting for connections");
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("accepted");

				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());

				String connectedRoleType = (String) in.readObject();

				if (connectedRoleType.equals("client")) {
					dealWithClientThreads[clientID] = new DealWithClient(socket, filesContentsList, in, out, clientID);
					dealWithClientThreads[clientID].start();
					clientID++;
				} else if (connectedRoleType.equals("worker")) {
					new DealWithWorker(socket, in, out).start();
				} else {
					System.out.println("Unknown role type connection attempt");
				}

			} catch (IOException e) {
				System.out.println("Connection ended");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			for (int i =0; i<clientID;i++) {
				System.out.println(i);
			}
			sendToClientMap();
		}
	}

	private static void addFilesFromFolderToList(final File folder) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				addFilesFromFolderToList(fileEntry);
			} else {
				Scanner scanner;
				try {
					scanner = new Scanner(fileEntry, "UTF-8");
					String text = scanner.useDelimiter("\\A").next();
					scanner.close();
					filesContentsList.add(text);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendToClientMap() {
		try {
			dealWithClientThreads[0].outToClient.writeObject("Tens um novo amigo");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.broker();

	}

}
