package dev.solidsilver;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SocketServer<T extends SocketJob> {

    private int port;
    private ServerSocket listener;
    private ThreadPool tpool;
    private ThreadManager tman;
    public boolean isRunning;

    protected SocketServer(int port) throws IOException {
        this.port = port;
        this.tpool = new ThreadPool();
        this.init();
        this.isRunning = false;
    }

    public void init() throws IOException {
        this.listener = new ServerSocket(this.port);
        this.tman = new ThreadManager(this.tpool, this.listener);
    }

    public void shutdown() {
        this.tman.terminate();
    }

    public ThreadManager getThreadManager() {
        return this.tman;
    }
 

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

    protected abstract T createSocketJob(Socket s);
    


}
