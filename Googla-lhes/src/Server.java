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


	public Server() {
		addFilesFromFolderToList(folder);
		try {
			serverSocket = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public synchronized List<String> getFilesContentsList(){
		return filesContentsList;
	}
	
	public void broker() {
		System.out.println("Waiting for connections");
		while (true)
			try {
				Socket socket = serverSocket.accept();
				System.out.println("accepted");
				
//				in = new ObjectInputStream(socket.getInputStream());
//				out = new ObjectOutputStream(socket.getOutputStream());	
//				
//				String connectedRoleType = (String) in.readObject();
//				
//				if (connectedRoleType.equals("client")) {
//					new DealWithClient(socket).start();
//				} 
//				else if (connectedRoleType.equals("worker")) {
//					new DealWithWorker(this).start();
//				}
//				else {
//					System.out.println("Unknown role type connection attempt");
//				}
				
				new DealWithClient(socket, filesContentsList).start();
				
				
			} catch (IOException e) {
				System.out.println("Connection ended");
			} 
//		catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
	}

	private static void addFilesFromFolderToList(final File folder) {
		
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				addFilesFromFolderToList(fileEntry);
			} else {
				Scanner scanner;
				try {
					scanner = new Scanner(fileEntry, "UTF-8" );
					String text = scanner.useDelimiter("\\A").next();
					scanner.close();
					filesContentsList.add(text);	
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.broker();

	}
	

}
