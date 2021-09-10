package dev.solidsilver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

import java.util.regex.Matcher;

public class DefaultSocketJob extends SocketJob {

  private ThreadManager tm;

  public DefaultSocketJob(Socket socket, ThreadManager tm) {
    super(socket);
    this.tm = tm;

  }

  @Override
  public void run() {
    try {
      boolean test = false;
      if (test) {
        Thread.sleep(100);

      }
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String cmd = in.readLine();
      String input;
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      Pattern p = null;
      Matcher m = null;
      input = cmd.toUpperCase();

      String regex = "(((ADD)|(SUB)|(MUL)|(DIV)|(POW)),[0-9]+,[0-9]+)|(KILL)|(EHLO)";
      p = Pattern.compile(regex);
      m = p.matcher(input);

      if (!m.matches()) {
        out.println("Unsupported commands. Try again. Examples of possible commands are "
            + "ADD,4,5 or SUB,3,6 or MUL,9,8 or DIV,1,1 or POW,2,3 or KILL.");
      } else {
        processInput(input, out);
      }
    } catch (IOException e) {
      log("Error handling client: " + e);
    } catch (InterruptedException e) {
      log("could not sleep");
      ;
    } finally {
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

    String[] tokens = input.split(",");

    int operand1 = Integer.parseInt(tokens[1]);
    int operand2 = Integer.parseInt(tokens[2]);
    String send = "";
    if (input.charAt(0) == 'A') {
      send = "" + (operand1 + operand2);

    } else if (input.charAt(0) == 'S') {
      send = "" + (operand1 - operand2);
    } else if (input.charAt(0) == 'M') {
      send = "" + (operand1 * operand2);
    } else if (input.charAt(0) == 'D') {
      if (operand2 == 0) {
        send = "Undefined!";
      } else {
        send = "" + (operand1 / operand2);
      }
    } else if (input.charAt(0) == 'P') {
      send = "" + Math.pow(operand1, operand2);
    }
    out.println(send);
    log("Sent: " + send);
  }

  private void log(String message) {
    System.out.println("WT: " + message);
  }

}
