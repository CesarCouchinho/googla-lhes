import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DealWithWorker extends Thread {

	private Server server;
	private ObjectInputStream inFromWorker;
	private ObjectOutputStream outToWorker;
	private int workerID;
	private Task currentTask;

	public DealWithWorker(Server server, ObjectInputStream inFromWorker, ObjectOutputStream outToWorker, int id) {
		System.out.println("DealWithWorker thread created");
		this.server = server;
		this.inFromWorker = inFromWorker;
		this.outToWorker = outToWorker;
		this.workerID = id;
	}

	@Override
	public void run() {
		try {
			while (true) {
				currentTask = server.queue.get();
				outToWorker.writeObject(currentTask);

				int count = (int) inFromWorker.readObject();

				if (count != 0) {
					server.addToMap(currentTask.content, count);
					System.out.println("DealWithWorker added to map, content with count " + count + " from task sent");
				}
				server.incrementTasksCompleted();

			}
		} catch (IOException e) {
			System.out.println("Connection ended by Worker" + workerID);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
