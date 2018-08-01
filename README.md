# **Design:**

## Classes:

 The **TaskServer** class is responsible for running the main server, and starting the ThreadPool and ThreadManager. It is also responsible for accepting incoming connections and sending off the jobs to the ThreadPool.

 The **Job** class is responsible for receiving a socket connection and a command from the TaskServer. When the Job class first runs, it grabs the command from the outputstream of the received socket.  Then, it uppercases the command if it is not already so and makes sure if it is a valid command.  If the command is valid, the Job will process it accordingly and sends back the result back to the corresponding client. Otherwise, an unsupported commands message will appear in the user interface of the client and the connection will then be closed.

 The **ThreadPool** class is responsible for starting and maintaining an array of threads that collect and run jobs from a jobQueue. It also can increase and decrease the number of pooled threads on request, as well as gracefully terminate all threads in the pool.

 The **WorkerThread** class is responsible for monitoring the jobQueue, taking a job when available, and running the given job. It goes through this process as long as the TaskServer is running.

 The **ThreadManager** class is responsible for watching the ThreadPool&#39;s resources. If the number of jobs to complete passes a threshold, it calls upon the ThreadPool to increase/decrease the number of threads to scale to the demand.

# **Issues and How We Solved Them:**

**        1:** One issue we had developing this program is that an Interrupted Exception would be thrown when a WorkerThread receives a &quot;KILL&quot; Job. The problem was that the method called by the thread would hang until all of the threads have been killed. This cause all threads but the thread that called &quot;KILL&quot; to terminate, and then the ThreadPool would throw an exception when trying to kill the last thread.

  **1A:** We fixed this problem by changing the method called by the job to kill the entire server. The method now sets a boolean in the ThreadManager, and does not wait for the all threads to be stopped. Thus, all jobs are finished before the ThreadPool is killed.

  **2.** Second issue was that main would not exit once the ThreadManger and ThreadPool had shut down. The problem was it was waiting at listener.accept(), and would not move forward without a new connection.

  **2A:** We fixed this problem by passing the ServerSocket listener into the ThreadManger, and calling listener.close() from within ThreadManger right before it stopped. This way, listener.accept() in main would throw an exception, which we could catch and use to completely stop the server.



# **Tests:**

## **Test 1:** Force an increase of the ThreadPool:
![TEST1](/public/TESTincthread.png)
As can be seen in the above screenshot, as the number of incoming connections increases, the ThreadPool (@@@TP above) doubles the thread twice.


## **Test 2:** Observe decrease of pool after load decreases:
![TEST2](/public/TESTdecthread.png)
As can be seen above, as a number of connections are closed (and only one opened), the ThreadPool halves the number of threads.


## **Test 3:** Graceful kill:
![TEST3](/public/TESTkill.png)
_Server(left), Client(right)_
As seen above, after a few normal commands the client sends &quot;kill&quot;. After that, the server closes all of the current threads not processing a command (and waiting for those that are), then shuts down.



## **Test 4:** Response to commands
![TEST4](/public/TESTcmds.png)
As can be seen above, commands ADD, SUB, MUL, and DIV return correct results. Other commands result in a message notifying the client of available commands and usage.


## **Test 5:** Full Job Queue
![TEST5](/public/TESTfuljq.png)
_Server(left), Client(right)_
This test increased the latency of commands on the WorkerThreads and sent 10 commands from 100 threads. As shown, once the server logs a full jobQueue, the client receives a message indicating this.



GitHub Link: [https://github.com/Solidsilver/cscd-467-midterm](https://github.com/Solidsilver/cscd-467-midterm)