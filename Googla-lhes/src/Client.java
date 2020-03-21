import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client {
	
	final static File folder = new File("src/news");
	final static List<String> filesNamesList = new ArrayList<>(10);
	final static List<String> filesHeader = new ArrayList<>(10);
	
	public static void addFilesFromFolderToList(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				addFilesFromFolderToList(fileEntry);
			} else {
				filesNamesList.add(fileEntry.getName());
			}
		}
	}
	
	public static String readHeaderFromFileName(String fileName) throws IOException {
		// Open the file
		FileInputStream fstream = new FileInputStream("src/news/" + fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

				
		String strLine;
		String header = new String();
		String article = new String();
		Boolean isHeader = true;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			if (isHeader) {
				header = strLine;
				isHeader = false;
			} else {
				article = strLine;
			}
		}

		return header;
	}
	
	public static String readArticleFromFileName(String fileName) throws IOException {
		// Open the file
		FileInputStream fstream = new FileInputStream("src/news/" + fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		String header = new String();
		String article = new String();
		Boolean isHeader = true;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			if (isHeader) {
				header = strLine;
				isHeader = false;
			} else {
				article = strLine;
			}
		}
		
		return article;
	}
	
	public static ArrayList<Integer> searchForKeyWordInFiles(String keyword) throws IOException {
		ArrayList<Integer> keywordMatchingIndexes=new ArrayList<Integer>();

		int i = 0;
		for (String fileName : filesNamesList) {
			if (readArticleFromFileName(fileName).contains(keyword) || readHeaderFromFileName(fileName).contains(keyword)) {
				keywordMatchingIndexes.add(i);
			}
			i++;
		}

		return keywordMatchingIndexes;
	}
	
	public static void main(String[] args) throws IOException {
//		drawGUI(1400, 800);
	}
	
}

//TODO: fazer com que ele leia o que esta nas files denntro do diretorio. E seperar o titulo do resto do texto, por bold o titulo da noticia