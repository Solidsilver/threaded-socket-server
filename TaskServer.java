import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TaskServer {

    public static void main(String[] args) throws Exception {
        System.out.println("The Task server is running.");
        ServerSocket listener = new ServerSocket(9898);
        ThreadPool tpool = new ThreadPool();
        ThreadManager tman = new ThreadManager(tpool, listener);
        tman.start();
        try {
            while (!tman.isTerminated()) {
                Socket s = listener.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String cmd = in.readLine();
                if (!(tpool.execute(new Job(s, cmd, tman)))) {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    System.out.println("TS: JobQueue full! Not accpting client");
                    out.println("The server is currently busy, please connect later!");
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
        System.exit(0);
    }

    private static class Job implements Runnable {
        private Socket socket;
        private ThreadManager tm;
        private String cmd;

        public Job(Socket socket, String command, ThreadManager tman) {
            this.socket = socket;
            this.tm = tman;
            this.cmd = command;
            log("New connection at " + socket);
        }

        public void run() {
            try {
                boolean test = true;
                if (test) {
                    Thread.sleep(100);

                }
                String input;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Pattern p = null;
                Matcher m = null;
                input = cmd.toUpperCase();

                String regex = "(((ADD)|(SUB)|(MUL)|(DIV)),[0-9],[0-9])|(KILL)|(EHLO)";
                p = Pattern.compile(regex);
                m = p.matcher(input);

                if (!m.matches()) {
                    out.println("Unsupported commands. Try again. Examples of possible commands are "
                            + "ADD,4,5 or SUB,3,6 or MUL,9,8 or DIV,1,1 or KILL.");
                } else {
                    processInput(input, out);
                }
            } catch (IOException e) {
                log("Error handling client: " + e);
            } catch (InterruptedException e) {
                log("could not sleep");
;            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client closed");
            }
        }

        private void processInput(String input, PrintWriter out) {
            log("Request:: " + input);
            
            if (input.equals("KILL")) {
                tm.terminate();
                log("Kill sent, closing thread");
                return;
            }
            if (input.equals("EHLO")) {
                out.println("Hello, you are connected to the server");
                log("Sent greeting");
                return;
            }

            int operand1 = Integer.parseInt(input.charAt(4) + "");
            int operand2 = Integer.parseInt(input.charAt(6) + "");
            String send = "";
            if (input.charAt(0) == 'A') {
                send = "" + (operand1 + operand2);
                //out.println(operand1 + operand2);
                
            } else if (input.charAt(0) == 'S') {
                send = "" + (operand1 - operand2);
                //out.println(operand1 - operand2);
            } else if (input.charAt(0) == 'M') {
                send = "" + (operand1 * operand2);
                //out.println(operand1 * operand2);
            } else if (input.charAt(0) == 'D') {
                if(operand2 == 0) {
                    send = "Undefined!";
                  //out.println("Undefined!");
                }
                else {
                    send = "" + (operand1 / operand2);
                  //out.println(operand1 / operand2);
                }
            }
            out.println(send);
            log("Sent: " + send);
        }

        private void log(String message) {
            System.out.println("WT: " + message);
        }
    }
}
