package dev.solidsilver;
import java.util.LinkedList;

/**
 * A thread-safe queue.
 * @author solidsilver
 * @param <T> Type of data to be stored in the queue.
 * 
 */
public class SharedQueue<T>   {
	private LinkedList<T> data;
	private int cap;
	
	/**
	 * Creates a new queue with the specified capacity.
	 * @param size Capacity of the queue.
	 */
	public SharedQueue(int size) {
		this.cap = size;
		this.data = new LinkedList<T>();
	}
	
	/**
	 * Removes and returns the first element in the queue.
	 * @return The first element in the queue.
	 * @throws InterruptedException
	 */
	public synchronized T dequeue() throws InterruptedException {
		while (data.isEmpty()) {
				this.wait();
		}
		this.notifyAll();
		return data.removeFirst();
	}
	
	/**
	 * Adds an element to the queue.
	 * @param data Element of type T to be added.
	 * @throws InterruptedException
	 */
	public synchronized void enqueue(T data) throws InterruptedException {

		while (this.data.size() == this.cap) {
				this.wait();
		}
		this.data.add(data);
		this.notifyAll();
	}

	/**
	 * Returns the first element in the queue without removing it.
	 * @return The first element in the queue.
	 * 
	 */
	public synchronized T peek() {
		while (data.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.notifyAll();
		return data.peek();
	}
	
	
	public synchronized boolean isFull() {
		return this.cap == this.data.size();
	}
	
	public synchronized boolean isEmpty() {
		return this.data.isEmpty();
	}

	public synchronized int size() {
		return this.data.size();
	}
}
