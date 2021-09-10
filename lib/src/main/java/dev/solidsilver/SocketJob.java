package dev.solidsilver;
import java.net.Socket;

public abstract class SocketJob implements Runnable {
  protected Socket socket;

  public SocketJob(Socket socket) {
    this.socket = socket;
  }

}
