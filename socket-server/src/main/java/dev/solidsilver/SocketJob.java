package dev.solidsilver;
import java.net.Socket;

/**
 * A Runnable job that does work on a socket.
 * @author solidsilver
 */
public abstract class SocketJob implements Runnable {
  protected Socket socket;

  /**
   * Constructor.
   * @param socket The socket to work on.
   *
   */
  public SocketJob(Socket socket) {
    this.socket = socket;
  }

}
