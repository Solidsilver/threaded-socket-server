import java.util.concurrent.ArrayBlockingQueue;

public class ThreadPool {
	private int maxCapacity;
	private int actualNumberThreads;
	private WorkerThread[] holders;
	private boolean stopped;

	private ArrayBlockingQueue<Runnable> jobQueue;

	private class WorkerThread extends Thread {
		// this.jobQueue = new ArrayBlockingQueue<>(50);
		private boolean stopped;
		private Runnable toRun;
		private ArrayBlockingQueue<Runnable> jQueue;

		private WorkerThread(ArrayBlockingQueue<Runnable> jobQueue, boolean stopped) {
			this.jQueue = jobQueue;
			this.stopped = stopped;
		}

		/**
		 *  * Services this thread's client by first sending the  * client a welcome
		 * message then repeatedly reading strings  * and sending back the capitalized
		 * version of the string.  
		 */
		public void run() {
			while (!this.isInterrupted() && !stopped) {
				try {
					log("waiting for job");
					toRun = this.jQueue.take();
				} catch (InterruptedException e) {
					return;
				}
				//log("got job, running");
				toRun.run();
				//log("job done");
			}
		}

		private void log(String message) {
			System.out.println(message);
		}
	}

	public ThreadPool() {
		this.maxCapacity = 40;
		this.actualNumberThreads = 5;
		this.holders = new WorkerThread[maxCapacity];
		this.jobQueue = new ArrayBlockingQueue<>(50);
		this.stopped = false;
	}

	public void startPool() {
		for (int x = 0; x < 5; x++) {
			this.holders[x] = new WorkerThread(this.jobQueue, this.stopped);
			this.holders[x].start();
		}
	}

	public void stopPool() {
		for (int x = 0; x < actualNumberThreads; x++) {
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


	public void incresePool() {
		if (this.actualNumberThreads <= 20) {
			this.actualNumberThreads *= 2;
			for (int x = actualNumberThreads/2; x < actualNumberThreads; x++) {
				this.holders[x] = new WorkerThread(this.jobQueue, this.stopped);
				this.holders[x].start();
			}
		}
	}

	public void decreasePool() {
		if (this.actualNumberThreads > 5) {
			this.actualNumberThreads /= 2;
			for (int x = actualNumberThreads; x < actualNumberThreads*2; x++) {
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

	public void execute(Runnable r) {
		this.jobQueue.add(r);
	}

	public int numThreadsRunning() {
		return this.actualNumberThreads;
	}

	public int jobCount() {
		return this.jobQueue.size();
	}

}