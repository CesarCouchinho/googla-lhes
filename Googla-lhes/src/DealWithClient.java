import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DealWithClient extends Thread{

	ObjectInputStream inFromClient;
	ObjectOutputStream outToClient;
	private List <String> filesContentsList;
	private List<String> filteredList = new ArrayList<>(10);

	
	public DealWithClient(Socket socket, List <String> filesContentsList) {
		this.filesContentsList = filesContentsList;
		try {
			inFromClient = new ObjectInputStream(socket.getInputStream());
			outToClient = new ObjectOutputStream(socket.getOutputStream());	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String filter = (String) inFromClient.readObject();
				System.out.println("Server received from client: " + filter);

				filterListByKeyword(filter);

				outToClient.writeObject(filteredList);
				System.out.println(
						"Server sent to client filtered list by: " + filter + " (size: " + filteredList.size() + ")");

				outToClient.reset();
			}
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

	}

	public void filterListByKeyword(String filter) {
		filteredList.clear();
		for (String s : filesContentsList) {
			if (s.contains(filter)) {
				filteredList.add(s);
			}
		}
	}

}
