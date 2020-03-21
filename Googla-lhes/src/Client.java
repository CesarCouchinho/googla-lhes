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
	
	public static void drawGUI (int width, int height) throws IOException {
		JFrame frame= new JFrame("ISCTE Searcher");
		frame.setLayout(new BorderLayout()); 
		
		//North: Search Panel
		JPanel searchPanel = new JPanel();
		FlowLayout searchPanelLayout = new FlowLayout();
		searchPanel.setLayout(searchPanelLayout);
		JTextField searchText = new JTextField("", 25);
		searchPanel.add(searchText);
		JButton searchButton = new JButton("Search");
		searchPanel.add(searchButton);
		
		
		//South: Titles and Text Panel
		JPanel titlesAndTextPanel = new JPanel();
		BorderLayout titlesAndTextLayout = new BorderLayout();
		titlesAndTextLayout.setVgap(0);
		titlesAndTextPanel.setLayout(titlesAndTextLayout);
		
		//Titles Panel
		JPanel titlesPanel = new JPanel();
		addFilesFromFolderToList(folder);
		ArrayList<Integer> keywordMatchingIndexes = searchForKeyWordInFiles("Marcelo");
		for (int index : keywordMatchingIndexes) {
			filesHeader.add(readHeaderFromFileName(filesNamesList.get(index)));
		}
		final JList<String> titlesList = new JList<String>(filesHeader.toArray(new String[filesHeader.size()]));	
		
		JScrollPane scroll = new JScrollPane(titlesList);
		scroll.setPreferredSize(new Dimension(width/2, height));
		titlesPanel.add(scroll);
		titlesPanel.setPreferredSize(new Dimension(width/2, height));
		titlesAndTextPanel.add(titlesPanel, BorderLayout.WEST); 
		
		frame.add(searchPanel, BorderLayout.NORTH); 
		//Text Panel
		JPanel textPanel = new JPanel();
		
		JTextArea textArea = new JTextArea ();
		
		titlesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                  String fileName = filesNamesList.get(titlesList.getSelectedIndex());
                  
          		try {
					textArea.setText(titlesList.getSelectedValue().toString() + "\n" + "\n" + readArticleFromFileName(fileName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                }
            }
        });
		
		textArea.setPreferredSize(new Dimension(width/2 -6, height -6));
		textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
		JScrollPane textScroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension((width/2), height));
		textPanel.add(textScroll);
		textPanel.setPreferredSize(new Dimension((width/2), height));
		titlesAndTextPanel.add(textPanel, BorderLayout.EAST); 
		
		
		frame.add(titlesAndTextPanel, BorderLayout.SOUTH); 

		frame.pack();
		frame.setLocation(2000, 100);
		frame.setResizable(true); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.setVisible(true);  
	}
	
	public static void main(String[] args) throws IOException {
		drawGUI(1400, 800);
	}
	
}

//TODO: fazer com que ele leia o que esta nas files denntro do diretorio. E seperar o titulo do resto do texto, por bold o titulo da noticia