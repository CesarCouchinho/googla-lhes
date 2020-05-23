import java.util.LinkedList;

public class TasksQueue {

	private LinkedList tasksQueue = new LinkedList();
	private int max;

	public TasksQueue(int numberOfTasks) {
		max = numberOfTasks;
	}

	public synchronized void put(Task task) {
		while (tasksQueue.size() == max) {
			try {
				System.out.println("Queue full, waiting...");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		tasksQueue.add(task);
		notifyAll();
	}

	public synchronized Task get() {
		while (tasksQueue.isEmpty()) {
			try {
				System.out.println("Queue empty, waiting...");
				wait();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notifyAll();
		return (Task) tasksQueue.removeFirst();
	}

}
