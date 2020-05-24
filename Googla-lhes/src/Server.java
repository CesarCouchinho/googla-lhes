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
import java.util.stream.Collectors;

public class Server {

	private static String port = "1337";
	private ServerSocket serverSocket;
	final static File folder = new File("src/news/");
	private final static List<String> filesContentsList = new ArrayList<>(10);
	private static int numberOfFiles;
	public TasksQueue queue;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private DealWithClient[] dealWithClientThreads = new DealWithClient[100];
	private DealWithWorker[] dealWithWorkerThreads = new DealWithWorker[100];
	private int clientID;
	private int workerID;
	private int clientIDBeingServed;
	public int tasksCompleted;
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private Map<String, Integer> sortedByCount = new HashMap<String, Integer>();
	
	public Server() {
		addFilesFromFolderToList(folder);
		queue = new TasksQueue(getNumberOfFiles());
		clientID = 0;
		workerID = 0;
		clientIDBeingServed = 0;
		tasksCompleted = 0;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Waits for connections and creates DealWithClient, or DealWithWorker threads, based on the message received by whoever connects
	 * 
	 */
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
					dealWithWorkerThreads[workerID] = new DealWithWorker(this, in, out, workerID);
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

	
	/** Adds all the content from the files on the specified folder to a String List
	 * 
	 * @param folder
	 */
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

	
	/** Creates the tasks for the queue, for the workers to execute
	 * 
	 * @param filter: search filter, sent by client
	 * @param clientID: client that requested the search with that filter
	 */
	public void createTasks(String filter, int clientID) {
		for (String content : filesContentsList) {
			Task task = new Task(content, filter);
			queue.put(task);
		}
		System.out.println("Server created tasks for filter: " +filter);
		clientIDBeingServed = clientID;
	}

	
	/** Adds to the map the content of a file and the count of how many times the requested filter occurs in said file's content
	 * 
	 * @param content
	 * @param count
	 */
	public synchronized void addToMap(String content, int count) {
		map.put(content, Integer.valueOf(count));
	}

	
	/** Get the total number of files extracted from the folder
	 * 
	 * @return the number
	 */
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	
	
	/** Increments the number of tasks completed
	 * Used to know when all the tasks are completed, later, to send to client
	 */
	public synchronized void incrementTasksCompleted() {
		tasksCompleted++;
	}
	
	/** Calls sort method and then sends the sorted map to the client that requested it, through the object stream 
	 * 
	 */
	public void sendSortedMapToClient() {
		sortedByCount = sortByValue(map);
		try {
			dealWithClientThreads[clientIDBeingServed].outToClient.writeObject(sortedByCount);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		tasksCompleted=0;
		map.clear();
		System.out.println("Server sent to client " + clientIDBeingServed + " filtered map " + "(size: " + sortedByCount.size() + ")");

	}
	
	
	/** Sorts the map that will be sent to client by number of times the filter appears in each file's content.
	 * 
	 * @param NewsTitlesAndWordCounts
	 * @return sorted map
	 */
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
