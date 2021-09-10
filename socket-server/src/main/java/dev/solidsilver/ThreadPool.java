package dev.solidsilver;

/**
 * A thread pool that can be used to execute tasks in parallel.
 * 
 * @author solidsilver
 */
public class ThreadPool {
	private int maxCapacity;
	private int actualNumberThreads;
	private WorkerThread[] holders;
	private boolean stopped;

	private SharedQueue<Runnable> jobQueue;

	private class WorkerThread extends Thread {
		private boolean stopped;
		private Runnable toRun;
		private SharedQueue<Runnable> jQueue;

		private WorkerThread(SharedQueue<Runnable> jobQueue, boolean stopped, int threadID) {
			this.jQueue = jobQueue;
			this.stopped = stopped;
			Thread.currentThread().setName("Thread-" + threadID);
		}

		public void run() {
			while (!stopped) {
				try {
					toRun = this.jQueue.dequeue();
					log(Thread.currentThread().getName() + " process request:");
					toRun.run();
				} catch (Exception e) {
					if (stopped) {
						log(Thread.currentThread().getName() + " Interrupted, exiting");
						return;
					}
				}
			}
			log(Thread.currentThread().getName() + " Exiting");
		}

		private void log(String message) {
			System.out.println("WT: "+ message);
		}
	}

	/**
	 * Creates a new thread pool with a max capacity of 40
	 */
	public ThreadPool() {
		this.maxCapacity = 40;
		this.actualNumberThreads = 5;
		this.holders = new WorkerThread[maxCapacity];
		this.jobQueue = new SharedQueue<Runnable>(50);
		this.stopped = false;
	}

	/**
	 * Creates a new thread pool with a max capacity of the given value
	 * 
	 * @param maxCapacity
	 */
	public ThreadPool(Integer maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.actualNumberThreads = 5;
		this.holders = new WorkerThread[maxCapacity];
		this.jobQueue = new SharedQueue<Runnable>(50);
		this.stopped = false;
	}

	/**
	 * Starts the thread pool with 5 threads
	 */
	public synchronized void startPool() {
		log("Starting Pool");
		for (int x = 0; x < this.actualNumberThreads; x++) {
			this.holders[x] = new WorkerThread(this.jobQueue, this.stopped, x);
			this.holders[x].start();
		}
	}

	/**
	 * Stops the thread pool
	 */
	public synchronized void stopPool() {
		log("Stopping pool");
		for (int x = 0; x < this.maxCapacity; x++) {
			if (this.holders[x] != null) {
				this.holders[x].stopped = true;
				this.holders[x].interrupt();
				log("Closing thread " + x);
			}
			
		}
		log("Waiting for threads to end jobs");
		for (int x = 0; x < this.maxCapacity; x++) {
			if (this.holders[x] != null) {
				try {
					this.holders[x].join();
					this.holders[x] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		System.out.println("Pool Closed");
	}


	/**
	 * Increases the number of threads in the thread pool
	 */
	public synchronized void incresePool() {
		log("Doubling pool: ");
		if (this.actualNumberThreads <= 20) {
			int start = this.actualNumberThreads;
			this.actualNumberThreads *= 2;
			log("\tCurrent running threads: " + this.actualNumberThreads);
			for (int x = start; x < actualNumberThreads; x++) {
				this.holders[x] = new WorkerThread(this.jobQueue, this.stopped, x);
				this.holders[x].start();
			}
		} else {
			log("Pool not doubled!!!!!!");
		}
	}

	/**
	 * Decreases the number of threads in the thread pool
	 */
	public synchronized void decreasePool() {
		log("Halving pool: ");
		if (this.actualNumberThreads > 5) {
			int end = this.actualNumberThreads;
			this.actualNumberThreads /= 2;
			log("\tCurrent running threads: " + this.actualNumberThreads);
			for (int x = actualNumberThreads; x < end; x++) {
				this.holders[x].stopped = true;
				this.holders[x].interrupt();
				System.out.println("Closing thread " + x);
				try {
					this.holders[x].join();
					System.out.println("Closed " + x);
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.holders[x] = null;
			}
		}
		
	}

	/**
	 * Adds a task to the thread pool
	 */
	public boolean execute(Runnable r) throws InterruptedException {
		if (this.jobQueue.isFull()) {
			return false;
		}
		this.jobQueue.enqueue(r);
		return true;
		
	}

	public synchronized int numThreadsRunning() {
		return this.actualNumberThreads;
	}

	public synchronized int jobCount() {
		return this.jobQueue.size();
	}

	private void log(String message) {
		System.out.println("@@@TP: " + message);
	}

}