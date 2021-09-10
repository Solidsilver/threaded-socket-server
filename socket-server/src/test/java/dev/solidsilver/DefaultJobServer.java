package dev.solidsilver;
import java.io.IOException;
import java.net.Socket;

public class DefaultJobServer extends SocketServer<DefaultSocketJob> {

  public DefaultJobServer() throws IOException {
    super(9898);
  }

  @Override
  protected DefaultSocketJob createSocketJob(Socket s) {
    return new DefaultSocketJob(s, this.getThreadManager());
  }

  public static void main(String[] args) {
    try {
      DefaultJobServer server = new DefaultJobServer();
      server.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
}
