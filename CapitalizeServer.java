import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A server program which accepts requests from clients to
 * capitalize strings.  When clients connect, a new thread is
 * started to handle an interactive dialog in which the client
 * sends in a string and the server thread sends back the
 * capitalized version of the string.
 *
 * The program is runs in an infinite loop, so shutdown in platform
 * dependent.  If you ran it from a console window with the "java"
 * interpreter, Ctrl+C generally will shut it down.
 */

//test
public class CapitalizeServer {

    /**
     * Application method to run the server runs in an infinite loop
     * listening on port 9898.  When a connection is requested, it
     * spawns a new thread to do the servicing and immediately returns
     * to listening.  The server keeps a unique client number for each
     * client that connects just to show interesting logging
     * messages.  It is certainly not necessary to do this.
     */
    private ThreadManager tman;
    private ThreadPool tpool;
    public static void main(String[] args) throws Exception {
        System.out.println("The capitalization server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        tman = new ThreadManager();
        tpool = new ThreadPool();
        try {
            while (true) {
                new Capitalizer(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class Capitalizer extends Thread {
        private Socket socket;
        private int clientNumber;

        public Capitalizer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the capitalized version of the string.
         */
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get commands from client and process them
                Pattern p = null;
                Matcher m = null;
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }
                    
                    input = input.toUpperCase();
                    
                    //Check input
                    String regex = "(((ADD)|(SUB)|(MUL)|(DIV)),[0-9],[0-9])|(KILL)";
                    p = Pattern.compile(regex);
                    m = p.matcher(input);
           
                    if(!m.matches()) {
                        out.println("Unsupported commands. Try again. Examples of possible commands are " +
                                    "ADD,4,5 or SUB,3,6 or MUL,9,8 or DIV,1,1 or KILL.");
                    }
                    else {
                        boolean doneYet = processInput(input);
                        if(doneYet) {
                           break;
                        }            
                    }
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }
        
        private boolean processInput(String input, PrintWriter out) {
           if(input.equals("KILL")) {
              //Need some way to kill all threads?
              return true;
           }
           
           int operand1 = (int)(input.charAt(5));
           int operand2 = (int)(input.charAt(7));
           if(input.charAt(0) == 'A') { out.println(operand1 + operand2); }
           else if(input.charAt(0) == 'S') { out.println(operand1 - operand2); }
           else if(input.charAt(0) == 'M') { out.println(operand1 * operand2); }
           else if(input.charAt(0) == 'D') { out.println(operand1 / operand2); }
           
           return false;
        }

        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
}
