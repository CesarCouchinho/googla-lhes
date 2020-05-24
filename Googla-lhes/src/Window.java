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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Window {

	private Client client;
	private JTextField searchText = new JTextField(15);
	private List<String> currentFilteredList = new ArrayList<>(700);
	private DefaultListModel<String> newsTitlesModel;
	@SuppressWarnings("rawtypes")
	private JList jList;

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

	
	/** Creates button and adds an action listener to it
	 * 
	 * @return created button
	 */
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

	
	/** Creates the NORTH part of the layout, with the search bar and button
	 * 
	 * @return
	 */
	private JPanel createTextFieldAndButtonPanel() {
		JPanel searchPanel = new JPanel();
		FlowLayout searchPanelLayout = new FlowLayout();
		searchPanel.setLayout(searchPanelLayout);
		searchPanel.add(searchText);
		searchPanel.add(createButton());

		return searchPanel;
	}

	
	/** Creates the WEST part of the layout, with the list where the news' titles are shown
	 * 
	 * @param width
	 * @param height
	 * @return created panel
	 */
	private JPanel createNewsTitleSidePanel(int width, int height) {
		// Titles Panel
		JPanel titlesPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setPreferredSize(new Dimension(width / 2, height));
		titlesPanel.add(scrollPane);
		titlesPanel.setPreferredSize(new Dimension(width / 2, height));
		return titlesPanel;
	}

	
	/** Creates the EAST part of the layout, with the list where the news' articles are shown
	 * Also adds a listener to the JList for what news entry is being selected, in order to show the correct article for it
	 * @param width
	 * @param height
	 * @return created panel
	 */
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

	
	/** Updates window
	 * 
	 * @param sortedByCountedMap
	 */
	@SuppressWarnings("unchecked")
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
