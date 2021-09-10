package dev.solidsilver;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class TestClient extends Thread {
	private BufferedReader in;
	private PrintWriter out;
	private String serverAddress;
	private int serverPort;

	public TestClient() {
		Scanner kb = new Scanner(System.in);
		boolean connected = false;
		while (!connected) {
			out("Enter server address: ");
			this.serverAddress = kb.nextLine();
			try {
				sendMessage("EHLO");
				connected = true;
			} catch (IOException e) {
				outln("Could not connect");
			}
		}
	}

	public TestClient(String addr) {
		this.serverAddress = addr;
		this.serverPort = 9898;
	}


	public void sendMessage(String message) throws IOException {
		Socket socket = new Socket(serverAddress, this.serverPort);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		out.println(message);
		String response;
		try {
			response = in.readLine();
			if (response == null || response.equals("")) {
				System.out.println("Client terminated");
				return;
				//System.exit(0);
			}
		} catch (IOException ex) {
			response = "Error: " + ex;
			System.out.println("" + response + "\n");
		}
		outln(response);
	}

	public static void out(String s) {
		System.out.print(s);
	}

	public static void outln(String s) {
		System.out.println(s);
	}

	@Override
	public void run() {
		String[] messages = { "ADD", "SUB", "MUL", "DIV", "POW" };
		Random rand = new Random();
		for (int x = 0; x < 1000; x++) {
			try {
				String msg = messages[rand.nextInt(messages.length)];
				sendMessage(msg + "," + rand.nextInt(99) + "," + rand.nextInt(98)+1);
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		TestClient tc1 = new TestClient();
		String in;
		do {
			Scanner kb = new Scanner(System.in);
			out("> ");
			in = kb.nextLine();
			if (!in.equals("ext")){
				try {
					tc1.sendMessage(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} while (!in.equals("ext"));
	}
}