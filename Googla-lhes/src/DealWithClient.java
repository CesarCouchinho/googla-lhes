import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DealWithClient extends Thread{

	Server server;
	private List <String> filesContentsList;
	private List <String> filteredList = new ArrayList<>(10);
	private Map <String,Integer> sortedByCount = new HashMap<String, Integer>();
	ObjectInputStream inFromClient;
	ObjectOutputStream outToClient;

	
	public DealWithClient(Socket socket, List <String> filesContentsList) {
		System.out.println("DealWithClient thread created");
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
				sortListByKeywordOccurences(filteredList, filter);

				outToClient.writeObject(sortedByCount);
				System.out.println("Server sent to client filtered list by: " + filter + " (size: " + filteredList.size() + ")");

				outToClient.reset();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Connection ended by Client");
		} 

	}

	
	/** Filters the list by keyword passed (filter)
	 * 
	 * @param filter: word to filter by
	 */
	public void filterListByKeyword(String filter) {
		
		filteredList.clear();
		for (String s : filesContentsList) {
			if(s.contains(filter)) {
				filteredList.add(s);
			}
		}
	}
	
	
	/**sorts list given, by the number of times a keyword (filter) occurs on each entry of said list
	 * 
	 * @param list: list to sort
	 * @param filter: keyword to have the list sorted by (descending)
	 */
	public void sortListByKeywordOccurences (List<String> list,  String filter){
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String s : list) {
			int occurences = countOccurences(s, filter);
			map.put(s, Integer.valueOf(occurences));
		}
		
		sortedByCount = sortByValue(map);
	}
	
	
	/**
	 * 
	 * @param NewsTitlesAndWordCounts: Map with the -entry- being a give news Title. 
	 * And its -value- the number of times the filter occurred in it
	 * @return the map, sorted (descending) by the number of times the filter occurred
	 */
	public static Map<String,Integer> sortByValue (Map <String,Integer> NewsTitlesAndWordCounts){
        return NewsTitlesAndWordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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
}
