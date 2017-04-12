// By Greg Ozbirn, University of Texas at Dallas
// Adapted from example at Sun website: 
// http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
// 11/07/07

import java.io.*;
import java.util.Scanner;
import java.net.*;

public class SocketClient
{
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;

	public void communicate()
	{
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter your name : ");		// Enter the name 
		String name = sc.nextLine();
		out.println(name);	// Send user name to Server

		String choice ="";
		while (!choice.equals("7")){
			displayChoice();		// Display menu
			choice = sc.nextLine();	// Take the user's choice
			switch (choice){
			case "1":
				System.out.println("Known Users:");
				sendServer(choice);
				displayInfo(choice);
				break;
			case "2":
				System.out.println("Connected Users:");
				sendServer(choice);
				displayInfo(choice);
				break;
			case "3":
				sendServer(choice);
				System.out.println(readServer());
				break;
			case "4":
				sendServer(choice);
				System.out.println(readServer());
				break;	
			case "5":
				sendServer(choice);
				System.out.println(readServer());
				break;
			case "6":
				System.out.println("Your Messages:");
				sendServer(choice);
				displayInfo(choice);
				break;
			case "7":
				out.println(choice);
			}	
		}

	}
	public String readServer(){
		try {
			return (in.readLine());
		} catch (NumberFormatException | IOException e) {
			return null;
		}	
	}

	public void displayInfo(String choice){
		String message;	
		int i = 0;
		while((message = readServer())!= null){
			if (!message.equals("stop")){
				System.out.println("\t"+(i+1)+". "+message);	
			}else{
				break;
			}
		}	
	}

	public void sendServer(String choice){
		String name 	= "\n";
		String message 	= "\n";
		Scanner sc = new Scanner(System.in);
		int n = Integer.parseInt(choice);
		
		if ( n != 1 && n != 2 && n != 6){
			if (n==3){
				System.out.print("Enter recipient's name: ");
				name = name+sc.nextLine();
			}
			System.out.print("Enter a message: ");
			message = message+sc.nextLine();
			out.println(choice+name+message);
		}else{
			out.println(choice);
		}
	}

	public void displayChoice(){
		System.out.print("\n\n1. Display the names of all known users.\n"
				+ "2. Display the names of all currently connected users.\n"
				+ "3. Send a text message to a particular user.\n"
				+ "4. Send a text message to all currently connected users.\n"
				+ "5. Send a text message to all known users.\n"
				+ "6. Get my messages.\n"
				+ "7. Exit.\n"
				+ "Enter your choice: ");
	}
	public void listenSocket(String host, int port)
	{
		//Create socket connection
		try
		{
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch (UnknownHostException e) 
		{
			System.out.println("Unknown host");
			System.exit(1);
		} 
		catch (IOException e) 
		{
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage:  client hostname port");
			System.exit(1);
		}

		SocketClient client = new SocketClient();

		String host = args[0];
		int port = Integer.valueOf(args[1]);
		client.listenSocket(host, port);
		client.communicate();
	}
}
