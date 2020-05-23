import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DealWithWorker extends Thread {

	Server server;
	ObjectInputStream inFromWorker;
	ObjectOutputStream outToWorker;
	int workerID;
	Task currentTask;

	public DealWithWorker(Server server, Socket socket, ObjectInputStream inFromWorker, ObjectOutputStream outToWorker,
			int id) {
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
			System.out.println("Connection ended by Worker");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
