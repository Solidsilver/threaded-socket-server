
public class Tester {
	public static void main(String[] args) {
		/*TestClient tc1 = new TestClient();
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
		*/
		outln("Starting test");
		int clients = 100;
		TestClient[] tca = new TestClient[clients];
		for (int x = 0; x < clients; x++) {
			tca[x] = new TestClient("localhost");
			tca[x].start();
		}
		for (int x = 0; x < clients; x++) {
			try {
				tca[x].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		outln("Test Complete");

	}

	public static void out(String s) {
		System.out.print(s);
	}

	public static void outln(String s) {
		System.out.println(s);
	}
}