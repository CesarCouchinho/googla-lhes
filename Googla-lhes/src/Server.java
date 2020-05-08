import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {

	final static File folder = new File("src/news");
	final static List<String> filesNamesList = new ArrayList<>(10);

	public Server() {
		addFilesFromFolderToList(folder);

	}
	
	public static void addFilesFromFolderToList(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				addFilesFromFolderToList(fileEntry);
			} else {
				//List of the following type: [Fri20Oct2017049483888333GMT.txt , Fri20Oct201704948383223321GMT.txt , ...]
				filesNamesList.add(fileEntry.getName());
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		
	}
	
	
	
	
	
}
