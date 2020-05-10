import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingWorker;

public class ReadFilteredList extends SwingWorker<Void, String>{
	private List<String> filteredList;
	private JList<String> jList;

	public ReadFilteredList(List<String> filteredList, JList<String> jList) {
	    this.filteredList = filteredList;
	    this.jList = jList;
	}

	@Override
	protected Void doInBackground() throws Exception {
	    for (String s : filteredList) {
	    	String lines[] = s.split("\\r?\\n");
	    	publish(lines[0]);
	    }
	    return null;
	}

	@Override
	protected void process(List<String> chunks) {
		
	    DefaultListModel<String> model = (DefaultListModel<String>) jList.getModel();
	    model.clear();
	    
	    for (String s : chunks) {
	        model.addElement(s);
	    }
	}
	
}
