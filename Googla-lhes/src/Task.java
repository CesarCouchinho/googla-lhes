import java.io.Serializable;

public class Task implements Serializable{

	private static final long serialVersionUID = 1L;
	public String content;
	public String filter;

	public Task(String content, String filter) {
		this.content = content;
		this.filter = filter;
	}

}
