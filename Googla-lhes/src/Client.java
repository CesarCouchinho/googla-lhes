import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {

	final static File folder = new File("src/news");
	final static List<String> filesNamesList = new ArrayList<>(10);


	public Client() {
		addFilesFromFolderToList(folder);
	}

	public static void addFilesFromFolderToList(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				addFilesFromFolderToList(fileEntry);
			} else {
				filesNamesList.add(fileEntry.getName());
			}
		}
	}

//	public ArrayList<Integer> searchForKeyWordInFiles(String keyword) throws IOException {
//		ArrayList<Integer> keywordMatchingIndexes = new ArrayList<Integer>();
//
//		int i = 0;
//		for (String fileName : filesNamesList) {
//			if (readArticleFromFileName2(fileName).contains(keyword)
//					|| readHeaderFromFileName2(fileName).contains(keyword)) {
//				keywordMatchingIndexes.add(i);
//			}
//			i++;
//		}
//		System.out.println(keywordMatchingIndexes.toString());
//		return keywordMatchingIndexes;
//	}

	
	public static void main(String[] args) throws IOException {
		Client client = new Client();
		new GraphicalUI(client);
	}

}

//TODO: fazer com que ele leia o que esta nas files denntro do diretorio. E seperar o titulo do resto do texto, por bold o titulo da noticia