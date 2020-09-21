
/* A Java program for a Client */
import java.net.*;
import java.io.*; 
  
public class http_client
{ 
/* initialize socket and input output streams */
private Socket socket = null; 
private BufferedReader input = null; 
private DataOutputStream out = null; 
private DataInputStream in = null; 

/* constructor to put ip address and port */
public static void main(String args[])
{
	if (args.length < 2) {
		System.out.println("Client usage: java http_client URL");
	}
	else {
		http_client httpclient = new http_client(args[0]);
	}
}

public http_client(String url)
{

	try {
		HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
	} catch (IOException e) {
		e.printStackTrace();
	}

	/* establish a connection *//*
	try {
		socket = new Socket(address, port);
	} catch(Exception i) {
		System.out.println("Error in IP or port");
		System.exit(0);
    	}
	System.out.println("Connected");


	System.out.println("Write input to send to Server");

	try {
		*//* takes input from terminal *//*
		input = new BufferedReader(new InputStreamReader(System.in));

		*//* sends output to the socket *//*
		out = new DataOutputStream(socket.getOutputStream());

	} catch(IOException i) {
		System.out.println(i);
	}

	*//* string to read message from input *//*
	String line = "";

	*//* keep reading until "Over" is input *//*
	while (!line.equals("Over")) {
		try {
			line = input.readLine();
			out.writeUTF(line);
		} catch(Exception i) {
			System.out.println(i);
		}
	}

	*//* takes input from the Server socket *//*
	try {
		in = new DataInputStream(
		    new BufferedInputStream(socket.getInputStream()));

		String inputLine = "";

		System.out.println("Got input from Server...");
		System.out.println("Printing input: ");

		*//* reads message from Server until "Over" is sent *//*
		while (!inputLine.equals("Over"))
		{

			inputLine = in.readUTF();
		    System.out.println(inputLine);
		}

	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}


	*//* close the connection *//*
	try {
		input.close();
		out.close();
		socket.close();
	} catch(Exception i) {
		System.out.println(i);
	} */
}

}
