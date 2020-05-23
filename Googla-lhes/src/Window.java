import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Window {

	Client client;
	private JTextField searchText = new JTextField(15);
	List<String> currentFilteredList = new ArrayList<>(700);
	DefaultListModel<String> newsTitlesModel;
	@SuppressWarnings("rawtypes")
	JList jList;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Window(Client client) {
		this.client = client;
		JFrame frame = new JFrame("ISCTE Searcher");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel titlesAndTextPanel = new JPanel();
		BorderLayout titlesAndTextLayout = new BorderLayout();
		titlesAndTextLayout.setVgap(0);
		titlesAndTextPanel.setLayout(titlesAndTextLayout);
		newsTitlesModel = new DefaultListModel<String>();
		jList = new JList(newsTitlesModel);

		titlesAndTextPanel.add(createNewsTitleSidePanel(1200, 600), BorderLayout.WEST);
		titlesAndTextPanel.add(createNewsArticlesSidePanel(1200, 600), BorderLayout.EAST);

		frame.add(createTextFieldAndButtonPanel(), BorderLayout.NORTH);
		frame.add(titlesAndTextPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setLocation(400, 100);
		frame.setResizable(true);
		frame.setVisible(true);
	}

	public JButton createButton() {
		JButton button = new JButton("Search");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filter = searchText.getText();
				System.out.println("Window says: " + filter);

				client.sendFilterToServer(filter);
			}
		});
		return button;
	}

	private JPanel createTextFieldAndButtonPanel() {
		JPanel searchPanel = new JPanel();
		FlowLayout searchPanelLayout = new FlowLayout();
		searchPanel.setLayout(searchPanelLayout);
		searchPanel.add(searchText);
		searchPanel.add(createButton());

		return searchPanel;
	}

	@SuppressWarnings("rawtypes")
	private JList createJList() {
		@SuppressWarnings("unchecked")
		JList list = new JList(newsTitlesModel);
		list.setVisibleRowCount(6);
		return list;
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
						String content = currentFilteredList.get(jList.getSelectedIndex());
						String lines[] = content.split("\\r?\\n");

						try {
							textArea.setText(lines[0] + "\n" + "\n" + lines[1]);
						} catch (ArrayIndexOutOfBoundsException e) {
							textArea.setText(lines[0]);
						}

					} else {
						textArea.setText("");
					}
				}
			}
		});

		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane textScroll = new JScrollPane(textArea);
		textScroll.setPreferredSize(new Dimension((width / 2), height));
		textPanel.add(textScroll);
		return textPanel;
	}

	public void updateWindow(Map<String, Integer> sortedByCountedMap) {

		currentFilteredList.clear();
		for (Map.Entry<String, Integer> entry : sortedByCountedMap.entrySet()) {
			currentFilteredList.add(entry.getKey());
		}

		new ReadFilteredList(sortedByCountedMap, jList).execute();
		System.out.println("Window updated as requested");
		newsTitlesModel.clear();
	}

}
