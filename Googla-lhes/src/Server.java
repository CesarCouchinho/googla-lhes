import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Server {

	private static String port = "1337";
	private ServerSocket serverSocket;
	final static File folder = new File("src/news/");
	private final static List<String> filesContentsList = new ArrayList<>(10);
	static int numberOfFiles;

	TasksQueue queue;

	ObjectInputStream in;
	ObjectOutputStream out;
	DealWithClient[] dealWithClientThreads = new DealWithClient[100];
	DealWithWorker[] dealWithWorkerThreads = new DealWithWorker[100];
	int clientID;
	int workerID;
	Map<String, Integer> map = new HashMap<String, Integer>();
	private Map<String, Integer> sortedByCount = new HashMap<String, Integer>();

	int tasksCompleted = 0;
	private int clientBeingServed = 0;
	
	public Server() {
		addFilesFromFolderToList(folder);
		queue = new TasksQueue(numberOfFiles);
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
					dealWithClientThreads[clientID] = new DealWithClient(this, socket, in, out, clientID);
					dealWithClientThreads[clientID].start();
					clientID++;
				} else if (connectedRoleType.equals("worker")) {
					dealWithWorkerThreads[workerID] = new DealWithWorker(this, socket, in, out, workerID);
					dealWithWorkerThreads[workerID].start();
					workerID++;
				} else {
					System.out.println("Unknown role type connection attempt");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 

			for (int i = 0; i < clientID; i++) {
				System.out.println(i);
			}
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
					numberOfFiles++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sortAndSendMapToClient() {
		sortedByCount = sortByValue(map);
		try {
			dealWithClientThreads[clientBeingServed].outToClient.writeObject(sortedByCount);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		tasksCompleted=0;
		map.clear();
		System.out.println("Server sent to client " + clientBeingServed + " filtered map " + "(size: " + sortedByCount.size() + ")");

	}

	public void createTasks(String filter, int clientID) {
		for (String content : filesContentsList) {
			Task task = new Task(content, filter);
			queue.put(task);
		}
		System.out.println("Server created tasks for filter: " +filter);
		clientBeingServed = clientID;
	}

	public synchronized void addToMap(String content, int count) {
		map.put(content, Integer.valueOf(count));
	}

	public synchronized void incrementTasksCompleted() {
		tasksCompleted++;
	}
	
	public static Map<String, Integer> sortByValue(Map<String, Integer> NewsTitlesAndWordCounts) {
		return NewsTitlesAndWordCounts.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.broker();

	}

}
