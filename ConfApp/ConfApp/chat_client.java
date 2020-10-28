import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class chat_client implements Runnable{

	private chat_client partner;
	private boolean busy, connecting;
	private PrintWriter writer;
	private String name;
	private BufferedReader kbReader;

	public chat_client(String name, PrintWriter writer, BufferedReader kbReader){
		this.setName(name);
		this.setWriter(writer);
		this.setKbReader(kbReader);
	}

	public static void main(String[] args) {
		// Client needs server's contact information and user name
		if (args.length != 3) {
			System.out.println("usage: java ConfClient <host> <port> <name>");
			System.exit(1);
		}

		// Connect to the server at the given host and port
		Socket sock = null;
		try {
			sock = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println(
					"Connected to server at " + args[0] + ":" + args[1]);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		// Set up a thread to read from user and send to server
		try {
			// Prepare to write to socket with auto flush on
			PrintWriter toSockWriter =
					new PrintWriter(sock.getOutputStream(), true);

			String name = args[2];
			// Tell the server the user's name
			toSockWriter.println(name);

			// Prepare to read from keyboard
			BufferedReader kbReader = new BufferedReader(
					new InputStreamReader(System.in));

			// Spawn a thread to read from user and write to socket
			Thread child = new Thread(
					new chat_client(name, toSockWriter, kbReader));
			child.start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		// Now read from server and display to user
		try {
			// Prepare to read from socket
			BufferedReader fromSockReader = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));

			// Keep doing till server is done
			while (true) {
				// Read a line from the socket
				String line = fromSockReader.readLine();

				// Check if we got EOF on socket
				if (line == null)
					break;

				// Write the line to the user
				System.out.println(line);
			}
		} catch(SocketException e) {
			// Ignore potential socket closed exception
		} catch(Exception e) {
			e.printStackTrace();
		}

		// Exit to stop the child thread
		System.exit(0);
	}

	public void sendToPair(String msg){
		partner.getWriter().println(name + ": " + msg);
	}

	public void print(String msg){
		this.getWriter().println(msg);
	}

	public void pair(chat_client friend){
		partner = friend;
		this.setBusy(true);
	}

	public void unpair(){
		partner = null;
		this.setBusy(false);
	}

	public synchronized boolean isConnecting() {
		return connecting;
	}

	public synchronized void setConnecting(boolean connecting) {
		this.connecting = connecting;
	}

	public BufferedReader getKbReader() {
		return kbReader;
	}

	public void setKbReader(BufferedReader kbReader) {
		this.kbReader = kbReader;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public chat_client getPartner() {
		return partner;
	}

	public void setPartner(chat_client partner) {
		this.partner = partner;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	@Override
	public void run() {
		// Read from the keyboard and write to socket
		try {

			BufferedReader kbISR = new BufferedReader(new InputStreamReader(System.in));
			// Keep doing till user types EOF (Ctrl-D)
			while (true) {
				// Read a line from the user
				String line = getKbReader().readLine();

				/*BufferedReader kbISR = getKbReader();
				// Read a line from the user
				String line = kbISR.readLine();*/

				// If we get null, it means EOF, close socket
				if (line == null) {
					getWriter().close();
					break;
				}

				// Write the line to the socket
				getWriter().println(line);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
