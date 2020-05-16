import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingWorker;

public class ReadFilteredList extends SwingWorker<Void, String>{
	private List<String> filteredList;
	private Map <String,Integer> map = new HashMap<String, Integer>();;
	private JList<String> jList;

	public ReadFilteredList(Map<String,Integer> map, JList<String> jList) {
	    this.map = map;
	    this.jList = jList;
	}

	@Override
	protected Void doInBackground() throws Exception {
//		for (String s : filteredList) {
//	    	String lines[] = s.split("\\r?\\n");
//	    	publish(lines[0]);
//	    }
		for (Map.Entry<String,Integer> entry: map.entrySet()) {
			String lines[] = entry.getKey().split("\\r?\\n");
	    	publish(entry.getValue() + " - " +lines[0]);
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
