import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GraphicalUI {

	public void drawGUI (int width, int height) {
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
		
		frame.add(searchPanel, BorderLayout.NORTH); 
		
		//South: Titles and Text Panel
		JPanel titlesAndTextPanel = new JPanel();
		BorderLayout titlesAndTextLayout = new BorderLayout();
		titlesAndTextLayout.setVgap(0);
		titlesAndTextPanel.setLayout(titlesAndTextLayout);
		
		//Titles Panel
		JPanel titlesPanel = new JPanel();
		List<String> myList = new ArrayList<>(10);
		for (int index = 0; index < 200; index++) {
	         myList.add("List Item " + index);
	      }
		final JList<String> titlesList = new JList<String>(myList.toArray(new String[myList.size()]));		
		JScrollPane scroll = new JScrollPane(titlesList);
		scroll.setPreferredSize(new Dimension(width/2, height));
		titlesPanel.add(scroll);
		titlesPanel.setPreferredSize(new Dimension(width/2, height));
		titlesAndTextPanel.add(titlesPanel, BorderLayout.WEST); 
		
		//Text Panel
		JPanel textPanel = new JPanel();
		String text = new String ("Whisky, um requinte!\r\n" + 
				"É uma das bebidas mais conhecidas e consumidas em todo o mundo. A origem do whisky remonta o início do processo de destilação em si, descoberta e bastante utilizada pelos monges budistas para o processo de fabrico de perfumes. Países como Irlanda e Índia reivindicam a sua “paternidade”. Segundo os indianos, eles já produziam o whisky há cerca de 800 anos a.C., já os irlandeses dizem que o padroeiro do País, ST. Patrick, nos anos 400, fabricava uma bebida com os mesmos ingredientes do que conhecemos como whisky.A produção de whisky na Escócia provém de várias tradições que foram introduzidas por São Patrício da Irlanda no século IV ac. e a destilação era feita por indígenas nas terras altas, segundo relatos muito vagos.No sentido etimológico a palavra Whisky vem de “uisge”, forma abreviada do termo gaélico “uisge beatha” ou “aqua vitae” (água da vida/eau de vie/water of life). O que acaba tendo relação directa com esta bebida que alegra, relaxa e socializa. Apesar de consumido e produzido em várias partes do mundo, é o whisky escocês o de maior fama e credibilidade. Isso se deve sobretudo ao tempo e experiência no processo de destilação. O The Small House orgulha-se de ter uma requintada e exclusiva colecção de whiskys, produzidos em todo o mundo, como alguns whiskys da colecção Jameson, da colecção Macallan, The Glenlivet, Glenmorangie, The Balvenie, Nikka Whisky, entre muitos outros. Obras de arte que podem chegar a, por exemplo, reservas de 28 anos, ou mais.A nossa garrafeira é um must e a aposta em whiskys de requinte e raros vai ao encontro das nossas pretensões, que são trazer o melhor aos nossos visitantes.Desfrute de um bom momento, na companhia de um bom whisky, no The Small House.Estas bebidas possuem um elevado teor alcoólico, pelo que aconselhamos o seu consumo de forma ponderada e responsável.Para mais sobre nós, siga-nos no Facebook, Instagram e TripAdvisor.\r\n" + 
				"");
		
		JTextArea textArea = new JTextArea ();
		textArea.setPreferredSize(new Dimension(width/2 -6, height -6));
		textArea.setText(text);
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
	
	public static void main(String[] args) {
    }
	
}
