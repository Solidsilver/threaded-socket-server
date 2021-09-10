package dev.solidsilver;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple socket server that listens for connections on a specified port.
 * When a connection is received, it queues up a thread to handle the job
 * and then waits for another connection.
 * 
 * @author solidsilver
 */
public abstract class SocketServer<T extends SocketJob> {

    private int port;
    private ServerSocket listener;
    private ThreadPool tpool;
    private ThreadManager tman;
    public boolean isRunning;

    /**
     * Constructs a new SocketServer.
     * 
     * @param port The port to listen on.
     */
    protected SocketServer(int port) throws IOException {
        this.port = port;
        this.tpool = new ThreadPool();
        this.init();
        this.isRunning = false;
    }

    /**
     * Initializes the server with a new ServerSocket and ThreadManager.
     * 
     * @throws IOException
     */
    public void init() throws IOException {
        this.listener = new ServerSocket(this.port);
        this.tman = new ThreadManager(this.tpool, this.listener);
    }

    /**
     * Sends a signal to stop the server.
     */
    public void shutdown() {
        this.tman.terminate();
    }

    /**
     * Returns the current ThreadManager object
     */
    public ThreadManager getThreadManager() {
        return this.tman;
    }
 

    /**
     * Starts the server.
     * 
     * @throws Exception
     */
    public void run() throws Exception {
        if (!this.isRunning) {
            this.tman.start();
            this.isRunning = true;
            try {
                while (!tman.isTerminated()) {
                    Socket s = listener.accept();
                    if (!(tpool.execute(createSocketJob(s)))) {
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        System.out.println("TS: JobQueue full! Not accepting client");
                        out.println("The server is busy, try again later!");
                        s.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("ServerSocket closed");
            } finally {
                tman.join();
                if (!listener.isClosed()) {
                    listener.close();
                }
                System.out.println("Server Shut Down");
            }
            this.isRunning = false;
        }
    }

    /**
     * Creates a new SocketJob for the given Socket.
     * This method is called by the run() method, and should
     * be overridden by subclasses.
     * 
     * @param s The Socket to create a SocketJob for.
     */
    protected abstract T createSocketJob(Socket s);
    


}
