public class ThreadManager extends Thread {
	private final int T1 = 10, T2 = 20;
	private ThreadPool p;
	private boolean terminated;

	public ThreadManager(ThreadPool p) {
		this.p = p;
	}

	@Override
	public void run() {
		this.p.startPool();

		while(!terminated) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (p.jobCount() < T2 && p.numThreadsRunning() >= T2) {
				p.decreasePool();
			}
			if (p.jobCount() < T1 && p.numThreadsRunning() >= T1) {
				p.decreasePool();
			}
			if (p.jobCount() > T1 && p.numThreadsRunning() <= T1) {
				p.incresePool();
			}
			if(p.jobCount() > T2 && p.numThreadsRunning() <= T2) {
				p.incresePool();
			}
			
		}
	}

	public void terminate() {
		this.p.stopPool();
		this.terminated = true;
	}

}