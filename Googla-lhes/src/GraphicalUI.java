import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GraphicalUI {

	Client client;
	List<String> newsTitles = new ArrayList<>(10);
	List<String> currentlySelectedNewsFileNames = new ArrayList<>(10);
	private JTextField searchText = new JTextField(15);
	DefaultListModel<String> newsTitlesModel = new DefaultListModel<>();
	@SuppressWarnings("rawtypes")
	private JList jList = createJList();

	public GraphicalUI(Client client) throws IOException {
		this.client = client;
		JFrame frame = new JFrame("ISCTE Searcher");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//{ frame, width of window, length of window, X-position of window, Y-position of window } (IN PIXELS) 
		init(frame, 1200, 800, 400, 100);
	}

	public void init(JFrame frame, int width, int height, int xpos, int ypos) throws IOException {
		JPanel titlesAndTextPanel = new JPanel();
		BorderLayout titlesAndTextLayout = new BorderLayout();
		titlesAndTextLayout.setVgap(0);
		titlesAndTextPanel.setLayout(titlesAndTextLayout);
		titlesAndTextPanel.add(createNewsTitleSidePanel(width, height), BorderLayout.WEST);
		titlesAndTextPanel.add(createNewsArticlesSidePanel(width, height), BorderLayout.EAST);

		frame.add(createTextFieldAndButtonPanel(), BorderLayout.NORTH);
		frame.add(titlesAndTextPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setLocation(xpos, ypos);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public JButton createButton() {
		JButton button = new JButton("Search");
		button.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				String filter = searchText.getText();
				try {
					filterModel((DefaultListModel<String>) jList.getModel(), filter);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		return button;
	}

	@SuppressWarnings("rawtypes")
	private JList createJList() throws IOException {
		@SuppressWarnings("unchecked")
		JList list = new JList(createDefaultListModel());
		list.setVisibleRowCount(6);
		return list;
	}

	private ListModel<String> createDefaultListModel() throws IOException {
		for (String fileName : Client.filesNamesList) {
			currentlySelectedNewsFileNames.add(fileName);
			File text = new File("src/news/" + fileName);
			Scanner scnr = new Scanner(text, "UTF-8");

			newsTitles.add(scnr.nextLine());
			scnr.close();
		}

		for (String s : newsTitles) {
			newsTitlesModel.addElement(s);
		}
		return newsTitlesModel;
	}

	private JPanel createTextFieldAndButtonPanel() {
		JPanel searchPanel = new JPanel();
		FlowLayout searchPanelLayout = new FlowLayout();
		searchPanel.setLayout(searchPanelLayout);
		searchPanel.add(searchText);
		searchPanel.add(createButton());

		return searchPanel;
	}

	private JPanel createNewsTitleSidePanel(int width, int height) {
		// Titles Panel
		JPanel titlesPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setPreferredSize(new Dimension(width / 2, height));
		titlesPanel.add(scrollPane);
		titlesPanel.setPreferredSize(new Dimension(width / 2, height));
		return titlesPanel;
	}

	private JPanel createNewsArticlesSidePanel(int width, int height) {
		// Text Panel
		JPanel textPanel = new JPanel();
		JTextArea textArea = new JTextArea();
		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {

					if (jList.getSelectedIndex() >= 0) {
						String fileName = currentlySelectedNewsFileNames.get(jList.getSelectedIndex());

						try {
							textArea.setText(jList.getSelectedValue().toString() + "\n" + "\n"
									+ readArticleFromFileName(fileName));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						textArea.setText("");
					}
				}
			}
		});

		textArea.setPreferredSize(new Dimension(width / 2 - 6, height - 6));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane textScroll = new JScrollPane(textArea);
		textScroll.setPreferredSize(new Dimension((width / 2), height));
		textPanel.add(textScroll);
		textPanel.setPreferredSize(new Dimension((width / 2), height));
		return textPanel;
	}

	public void filterModel(DefaultListModel<String> model, String filter) throws IOException {
		model.clear();
		currentlySelectedNewsFileNames.clear();
		for (String filename : Client.filesNamesList) {
			File text = new File("src/news/" + filename);
			Scanner scnr = new Scanner(text, "UTF-8");
			Boolean isToAdd = new Boolean(false);

			while (scnr.hasNextLine()) {
				if (scnr.nextLine().contains(filter)) {
					isToAdd = true;
				}
			}
			if (isToAdd) {
				model.addElement(readTitleFromFileName(filename));
				currentlySelectedNewsFileNames.add(filename);
			}
			scnr.close();
		}

	}

	public String readArticleFromFileName(String fileName) throws IOException {
		File text = new File("src/news/" + fileName);
		Scanner scnr = new Scanner(text, "UTF-8");
		String article = new String();

		int lineNumber = 0;
		while (scnr.hasNextLine()) {
			if (lineNumber != 1) {
				article = scnr.nextLine();
			}
			lineNumber++;
		}
		scnr.close();
		return article;
	}

	public String readTitleFromFileName(String fileName) throws IOException {
		File text = new File("src/news/" + fileName);
		Scanner scnr = new Scanner(text, "UTF-8");
		String header = new String();
		int lineNumber = 0;
		while (scnr.hasNextLine()) {
			if (lineNumber == 0) {
				header = scnr.nextLine();
			}
			lineNumber++;
		}
		scnr.close();
		return header;
	}

}
