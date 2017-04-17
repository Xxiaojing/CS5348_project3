// By Greg Ozbirn, University of Texas at Dallas
// Adapted from example at Sun website: 
// http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
// 11/07/07

import java.io.*;
import java.util.Scanner;
import java.net.*;

public class Client
{
    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    public void communicate()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your name: ");

        while (true) {

            String name = sc.nextLine();

            //Send data over socket
            out.println(name);

            try
            {
                String line = in.readLine();
                if(line.equals("valid")) {

                    break;

                } else if(line.equals("full")) {

                    System.out.println("The server is full and can't take client any more.");
//                    System.exit(-1);
                    in.close();
                    out.close();

                } else {

                    System.out.println("The name you chose has been used by others. Please try a different one.");
                    System.out.println("Enter a different name: ");

                }

            }
            catch (IOException e)
            {
                System.out.println("Read failed");
                System.exit(1);
            }

        }

        while (true) {

            String choice = printMenu(sc);

            if(!(choice.equals("1")||choice.equals("2")||choice.equals("3")||
                    choice.equals("4")||choice.equals("5")||choice.equals("6")||choice.equals("7"))) {
                System.out.println("Bad choice!");
                continue;
            }

            requestInfo(choice, sc);

            if(choice.equals("7")) {

                break;

            } else {

                try
                {
                    String line;
                    int i = 1;
                    while ((line = in.readLine()) != null) {

                        if(!line.equals("stop")) {

                            if (choice.equals("3") | choice.equals("4") | choice.equals("5")) {

                                if(line.equals("full")) {
                                    System.out.println("The server is full, and the user talking to cannot be connected.");
                                } else {

                                    System.out.println(line);

                                }

                            } else if (choice.equals("6")) {

                                if(line.equals("empty")) {

                                    System.out.println("Message is empty");

                                } else {

                                    String time = in.readLine();
                                    String message = in.readLine();

                                    System.out.println("From " + line + ", " + time + ", " + message);

                                }

                            } else {

                                System.out.println( i + ". " + line + "\n");
                                i++;

                            }

                        } else {

                            break;

                        }

                    }

                }
                catch (IOException e) {

                    System.out.println("Read failed");
                    System.exit(1);

                }
            }

        }

    }

    public String printMenu(Scanner sc) {
        System.out.println("1. Display the names of all known users.\n" +
                "2. Display the names of all currently connected users.\n" +
                "3. Send a text message to a particular user.\n" +
                "4. Send a text message to all currently connected users.\n" +
                "5. Send a text message to all known users.\n" +
                "6. Get my messages.\n" +
                "7. Exit.\n" +
                "Enter your choice: ");
        String choice = sc.nextLine();

        return choice;

    }

    public void requestInfo(String choice, Scanner sc) {

        int length = 80;
        if(choice.equals("1")) {

            System.out.println("Known users: ");

        } else if(choice.equals("2")) {

            System.out.println("Currently connected users: ");

        } else if(choice.equals("3")) {

            System.out.println("Enter recipient's name: ");
            String name = sc.nextLine();
            System.out.println("Enter a message: ");
            String message = sc.nextLine();
            if(message.length() > length) {

                System.out.println("The message is too long and has been trimmed to standard.");
                message = message.substring(0, length);

            }
            choice = choice + "\n" + name + "\n" + message;

        } else if(choice.equals("4")) {

            System.out.println("Enter a message: ");
            String message = sc.nextLine();
            if(message.length() > length) {

                System.out.println("The message is too long and has been trimmed to standard.");
                message = message.substring(0, length);

            }
            choice = choice + "\n" + message;

        } else if(choice.equals("5")) {

            System.out.println("Enter a message: ");
            String message = sc.nextLine();
            if(message.length() > length) {

                System.out.println("The message is too long and has been trimmed to standard.");
                message = message.substring(0, length);

            }
            choice = choice + "\n" + message;

        } else if(choice.equals("6")) {

            System.out.println("Your messages: ");

        }
        else if(choice.equals("7")) {

            try {

                out.println(choice);
                out.close();
                in.close();

            } catch (Exception ex) {

                ex.printStackTrace();

            }

        }

        //Send data over socket
        out.println(choice);
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

        String hostname = "unknown";
        try {

            InetAddress address;
            address = InetAddress.getLocalHost();
            hostname = address.getHostName();

        } catch(UnknownHostException ex) {

            System.out.println("Hostname can not be resolved");
            ex.printStackTrace();

        }
        System.out.println("Connecting to " + hostname + " : " + port + "\n");

    }

    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage:  client hostname port");
            System.exit(1);
        }

        Client client = new Client();

        String host = args[0];
        int port = Integer.valueOf(args[1]);
        client.listenSocket(host, port);
        client.communicate();
    }
}
