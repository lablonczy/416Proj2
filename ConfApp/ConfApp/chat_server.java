import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class chat_server implements Runnable {

	private Socket clientSocket;

	private static LinkedList<chat_client> clients;

	public chat_server(Socket socket) {
		clientSocket = socket;
	}

	public static void main(String[] args) {
		// Server needs a port to listen on
		if (args.length != 1) {
			System.out.println("usage: java ConfServer <port>");
			System.exit(1);
		}

		// Be prepared to catch socket related exceptions
		try {
			// Create a server socket with the given port
			ServerSocket serverSock =
					new ServerSocket(Integer.parseInt(args[0]));

			// Keep track of active clients
			clients = new LinkedList<chat_client>();

			System.out.println("Waiting for clients ...");

			// Keep accepting/serving new clients
			while (true) {
				try {
					// Wait for another client
					Socket clientSock = serverSock.accept();

					// Spawn a thread to read/relay messages from this client
					Thread child = new Thread(new chat_server(clientSock));
					child.start();

					System.out.println("Waiting for clients ...");
				} catch (Exception e) {
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean addClient(chat_client client) {
		return clients.add(client);
	}

	public static synchronized boolean removeClient(chat_client client) {
		return clients.remove(client);
	}

	public static synchronized void pairClients(chat_client a, chat_client b) throws IOException {

		b.setConnecting(true);
		b.print(a.getName() + " is requesting to connect with you. Press Enter, then Y, then Enter, to accept, Enter twice to deny");

		String line = b.getKbReader().readLine();
		if (line.equals("Y")) {
			a.print(b.getName() + " has accepted your request");
			b.print("Successfully connect to " + a.getName());
			a.pair(b);
			b.pair(a);
		} else {
			b.print("Rejecting " + a.getName());
			a.print(b.getName() + " does not wish to chat with you");
		}
		b.setConnecting(false);

		relayIncludeServer(getAllStates());

	}

	public static synchronized void relayMessage(String mesg) {
		// Iterate through the list and send message to each client
		for (chat_client c : clients)
			c.print(mesg);
	}

	public static synchronized void relayIncludeServer(String mesg) {
		// Iterate through the list and send message to each client
		System.out.println(mesg);
		for (chat_client c : clients)
			c.print(mesg);
	}

	public static synchronized void relayMessage(chat_client exception, String mesg) {
		// Iterate through the list and send message to each client
		for (chat_client c : clients){
			if(c == exception)
				continue;
			c.print(mesg);
		}
	}

	public static synchronized void unpairClients(chat_client a, chat_client b) {
		a.unpair();
		b.unpair();
	}

	public synchronized void connect(chat_client client, String line) {
		if(line.equals(client.getName())){
			client.print("Cannot connect to self");
			return;
		}

		for(chat_client c : clients)
			if(c.getName().equals(line)  && (!c.isBusy() || (c.isBusy() && c.getPartner() == client))) {
				try {
					pairClients(client, c);
				} catch (Exception e) {
					client.print("Friend disconnected while connecting");
					return;
				}
				System.out.println("Paired " + client.getName() + " and " + c.getName());
				System.out.println(getAllStates());
			}
	}

	public static synchronized String getAllStates(){
		StringBuilder builder = new StringBuilder();

		builder.append("List of clients and states\n");

		for(chat_client client : clients)
			builder.append(client.getName()).append(" | ").append(client.isBusy()?"Busy":"Free").append("\n");

		return builder.toString();
	}

	private chat_client clientByName(String name){
		for(chat_client client : clients)
			if(client.getName().equals(name))
				return client;

		return null;
	}

	private boolean clientsHasName(String name){
		for(chat_client client : clients)
			if(client.getName().equals(name))
				return true;

		return false;
	}

	@Override
	public void run() {

		chat_client client = null;
		try(BufferedReader clientReader = new BufferedReader((new InputStreamReader(clientSocket.getInputStream()))); PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true)) {

			String clientName = clientReader.readLine();
			System.out.println("Client " + clientName + " accepted");

			client = new chat_client(clientName, clientWriter, clientReader);

			addClient(client);
			relayIncludeServer(getAllStates());

			String line = "";

			/*if(!client.isBusy()){
				client.print("No friend by that name currently connected");
				removeClient(client);
				return;
			}*/
			client.print("Who would you like to connect with?\n" + getAllStates());

			while (true) {

				line = clientReader.readLine();

				if(client.isConnecting())
					continue;


				if(line == null)
					break;
				else if(client.isBusy())
					client.sendToPair(line);
				else if(!client.isBusy()) {

					if (clientsHasName(line)) {
						chat_client temp = clientByName(line);
						if(temp.isBusy() || temp.isConnecting()){
							client.print(line + " is either currently negotiating connection or is currently connected");
							continue;
						}
						client.print("Attempting to connect " + client.getName() + " and " + line);
						connect(client, line);
					} else {
						client.print("Name not found");
					}
					client.print("Who would you like to connect with?\n" + getAllStates());
				}

			}



		} catch (IOException e) {
			if(client.isBusy())
				unpairClients(client, client.getPartner());
			removeClient(client);

			relayIncludeServer(client.getName() + " disconnected\n" + getAllStates() + "\n");
		}

	}
}
