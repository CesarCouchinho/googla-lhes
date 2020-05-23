import java.io.Serializable;

public class Task implements Serializable{

	String content;
	String filter;

	public Task(String content, String filter) {
		this.content = content;
		this.filter = filter;
	}

}
