
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

	public ThreadPool() {
		this.maxCapacity = 40;
		this.actualNumberThreads = 5;
		this.holders = new WorkerThread[maxCapacity];
		this.jobQueue = new SharedQueue<Runnable>(50);
		this.stopped = false;
	}

	public synchronized void startPool() {
		log("Starting Pool");
		for (int x = 0; x < 5; x++) {
			this.holders[x] = new WorkerThread(this.jobQueue, this.stopped, x);
			this.holders[x].start();
		}
	}

	public synchronized void stopPool() {
		log("Stopping pool");
		for (int x = 0; x < 40; x++) {
			if (this.holders[x] != null) {
				this.holders[x].stopped = true;
				this.holders[x].interrupt();
				log("Closing thread " + x);
			}
			
		}
		log("Waiting for threads to end jobs");
		for (int x = 0; x < 40; x++) {
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