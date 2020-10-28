/*
 * Implementation of a conference server in java
 * By Srihari Nelakuditi for CSCE 416
 */

// Package for I/O related stuff
import java.io.*;

// Package for socket related stuff
import java.net.*;

// Package for list related stuff
import java.util.*;

/*
 * This class does all the conf server's job
 * 
 * It consists of parent thread (code inside main method) which accepts
 * new client connections and then spawns a thread per connection
 * 
 * Each child thread (code inside run method) reads messages
 * from its socket and broadcasts the message to the all active connections
 * 
 * Since a thread is being created with this class object,
 * this class declaration includes "implements Runnable"
 */
public class ConfServer implements Runnable
{
	// Each instance has a separate socket
	private Socket clientSock;

	// Whole class keeps track of active clients
	private static ArrayList<PrintWriter> clientList;

	private static LinkedList<ConfClient> clients;

	// Constructor sets the socket for the child thread to process
	public ConfServer(Socket sock)
	{
		clientSock = sock;
	}
	
	// Add the given client to the active clients list
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized boolean addClient(PrintWriter toClientWriter)
	{
		return(clientList.add(toClientWriter));
	}

	/*public static synchronized boolean addClientByClient(ConfClient client){
		clients.add(client);
	}*/

	// Remove the given client from the active clients list
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized boolean removeClient(PrintWriter toClientWriter)
	{
		return(clientList.remove(toClientWriter));
	}

	// Relay the given message to all the active clients
	// Since all threads share this, we use "synchronized" to make it atomic
	public static synchronized void relayMessage(String mesg)
	{
		// Iterate through the list and send message to each client
		for (PrintWriter clientWriter : clientList)
            clientWriter.println(mesg);
	}

	// The child thread starts here
	public void run()
	{
		String clientName = "";
		// Read from the client and relay to other clients
		try {
			// Prepare to read from socket
			BufferedReader fromClientReader = new BufferedReader(
					new InputStreamReader(clientSock.getInputStream()));
			
			// Get the client name
			clientName = fromClientReader.readLine();
			System.out.println("Client " + clientName + " accepted");
			printAllClients();

			// Prepare to write to socket with auto flush on
			PrintWriter toClientWriter =
					new PrintWriter(clientSock.getOutputStream(), true);

			// Add this client to the active client list
			addClient(toClientWriter);

			// Keep doing till client sends EOF
			while (true) {
				// Read a line from the client
				String line = fromClientReader.readLine();

				// If we get null, it means client sent EOF
				if (line == null)
					break;

				// Send the line to all active clients
				relayMessage(clientName + ": " + line);
			}

			// Remove this client from active list
			removeClient(toClientWriter);
			
			// Done with the client, close everything
			toClientWriter.close();
		}
		catch (Exception e) {
			System.out.println(clientName + " left the conference");
			//System.out.println(e);
		}
	}

	public static synchronized void printAllClients(){
		System.out.println("List of clients and states");

	}

	/*
	 * The conf server program starts from here.
	 * This main thread accepts new clients and spawns a thread for each client
	 * Each child thread does the stuff under the run() method 
	 */
	public static void main(String[] args)
	{
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
			System.out.println("Waiting for clients ...");
			
			// Keep track of active clients
			clientList = new ArrayList<PrintWriter>();
			
			// Keep accepting/serving new clients
			while (true) {
				try {
					// Wait for another client
					Socket clientSock = serverSock.accept();

					// Spawn a thread to read/relay messages from this client
					Thread child = new Thread(new ConfServer(clientSock));
					child.start();
				} catch (Exception e) {
					break;
				}
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}