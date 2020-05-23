import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker {

	private static String port = "1337";

	ObjectOutputStream outToServer;
	ObjectInputStream inFromServer;
	
	public void connectToServer() {
		try {
			
			InetAddress host = InetAddress.getLocalHost();
			Socket workerSocket = new Socket(host, Integer.parseInt(port));
			
			outToServer = new ObjectOutputStream(workerSocket.getOutputStream());
			inFromServer = new ObjectInputStream(workerSocket.getInputStream());
			
			outToServer.writeObject("worker");
			
			while (true) {
				Object o;
				try {
					o = inFromServer.readObject();
					if (o instanceof Task) {
						Task task = (Task) o;
						executeTask(task);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			System.out.println("Worker out");
		}
	}
	
	public void executeTask(Task task) {
		String content = task.content;
		String filter = task.filter;
		int count = countOccurences(content, filter);
		try {
			outToServer.writeObject(count);
			if (count != 0) {
				System.out.println("Worker sent to server the count: " +count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/** counts the number of times a keyword (filter) occurs in a given string (content)
	 * 
	 * @param content: the string 
	 * @param filter: keyword to count how many times it occurs in the string
	 * @return the value of the counter
	 */
	public int countOccurences (String content, String filter) {
		int count = 0;
		Pattern p = Pattern.compile(filter);
		Matcher m = p.matcher(content);
		while (m.find()) {
		    count++;
		}
		return count;
	}
	
	
	public static void main(String[] args) {
		Worker worker = new Worker();
		worker.connectToServer();
	}
}
