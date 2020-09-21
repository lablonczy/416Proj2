
/* A Java program for a Client */
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
	if (args.length < 1) {
		System.out.println("Client usage: java http_client URL");
	}
	else {
		http_client httpclient = new http_client(args[0]);
	}
}

public http_client(String url) {

	String header = "", html = "";
	File output = new File("htttp_client_output");

	try {
		HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod("GET");

		if(connection.getResponseCode() == 302)
			connection = (HttpURLConnection)(new URL(connection.getHeaderField("Location")).openConnection());

		header = connection.getHeaderFields().toString();
		header = header.replaceAll("],", "]\n");


//		ByteArrayOutputStream html = new ByteArrayOutputStream();
		InputStream is = connection.getInputStream();
		System.out.println("done fetching");


		html = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

	} catch (IOException e) {
		e.printStackTrace();
	}


	try(PrintWriter writer = new PrintWriter(output)) {

		writer.println(header + "\n");
		writer.println(html);

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}



	/*function flipOneTwo(){
		var src1 = "http://abjkasdkjf.jpg";
		var src2 = "http://asdjkfhalskdjf.jpg";
		document.getElementById("img1").src = src2; //image 1 becomes image 2
		document.getElementById("img2").src = src1; //image 2 becomes image 1
	}

	function flipBackToOriginal(){
		var src1 = "http://abjkasdkjf.jpg";
		var src2 = "http://asdjkfhalskdjf.jpg";
		document.getElementById("img1").src = src1; //image 1 becomes image 1
		document.getElementById("img2").src = src2; //image 2 becomes image 2
	}

	<img onmouseover="flipOneTwo()" onmouseout="flipBackToOriginal" blah blah blah>*/

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
