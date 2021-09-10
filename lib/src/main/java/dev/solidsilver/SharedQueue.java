package dev.solidsilver;
import java.util.LinkedList;


public class SharedQueue<T>   {
	private LinkedList<T> data;
	private int cap;
	
	public SharedQueue(int size) {
		this.cap = size;
		this.data = new LinkedList<T>();
	}
	
	public synchronized T dequeue() throws InterruptedException {
		while (data.isEmpty()) {
				this.wait();
		}
		this.notifyAll();
		return data.removeFirst();
	}
	
	public synchronized void enqueue(T data) throws InterruptedException {

		while (this.data.size() == this.cap) {
				this.wait();
		}
		this.data.add(data);
		this.notifyAll();
	}

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
