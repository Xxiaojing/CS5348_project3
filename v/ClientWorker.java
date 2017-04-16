import java.io.*;
import java.net.Socket;
import java.text.*;
import java.util.*;

public class ClientWorker implements Runnable {

    private Socket client;
    private static ArrayList<User> userList; //Create a new User arraylist
    private User newUser = new User(); //Create a new customer referencing the user corresponding to the current client.
    private User coUser = new User(); // Create a customer referencing the user corresponding to the client that current client is talking to.
    private String status = "known"; // Create a variable to represent the client is a known client.

    BufferedReader in = null;
    PrintWriter out = null;

    ClientWorker(Socket client, ArrayList<User> userList)
    {
        this.client = client;
        this.userList = userList;
    }

    public void run() {

        String line;

        try {

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

        } catch (IOException e) {

            System.out.println("in or out failed");
            System.exit(-1);

        }

//        check whether the customer getting connected can be allowed and if it is allowed
//        whether need to add a new customer to the user list or not.
        newUser = userConnectionCheck(in, out, newUser, status);

        try {

            while ((line = in.readLine()) != null) {

                if (line.equals("1")) {

                    checkAllKnownUsers(line, null);

                } else if (line.equals("2")) {

                    checkAllConnectedUsers(line, null);

                } else if (line.equals("3")) {

                    String name = in.readLine();
                    String message = in.readLine();
                    String time = getTime();
                    String sendMessage = newUser.name + "\n" + time + "\n" + message;

//                    check whether the person sending message to is a known user or not.
//                    If he's unknown, add him to the user list.
                    int index = checkAUserStatus(name);
                    updateUser(index, name, sendMessage);

                } else if (line.equals("4")) {

                    String message = in.readLine();
                    checkAllConnectedUsers(line, message);

                } else if (line.equals("5")) {

                    String time = getTime();
                    String message = in.readLine();
                    checkAllKnownUsers(line, newUser.name + "\n" + time + "\n" + message);

                } else if (line.equals("6")) {

                    if(newUser.messageList.size() == 0) {

                        out.println("empty");

                    } else {

                        int i = 0;
                        while (i < newUser.messageList.size()) {

                            out.println(newUser.messageList.get(i));
                            i++;

                        }

                    }

                    System.out.println(getTime() + ", " + newUser.name + " gets messages.");

                } else if (line.equals("7")) {

                    newUser.setConnected(false);
                    System.out.println(getTime() + ", " + newUser.name + " exits.");
                    exitClient();
                    break;

                }

                out.println("stop");

            }

//            client.isConnected();

        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        }
    }

//        Check whether the name provide by the client is a valid name
//    and get a valid name eventually and add the user to the user list.
    public static synchronized User userConnectionCheck (BufferedReader in, PrintWriter out, User user, String status) {

        int index;
        String line = new String();

        while (true) {

            try {

                line = in.readLine();

            } catch (IOException e) {

                System.out.println("Read failed");
                System.exit(-1);

            }

            index = checkAUserStatus(line);
            if (index == -1) {

                boolean added = false;
                user.setName(line);
                added = addUser(user, out);
                if (added) {

                    out.println("valid");

                }

                break;

            } else {

                if(!checkAUserConnection(index)) {

                    out.println("valid");
                    user = userList.get(index);
                    break;

                } else {

                    out.println("invalid");

                }

            }

        }

        System.out.println(getTime() + ", " + "Connection by " + user.status + " user " + line);
        user.setStatus(status);
        user.setConnected(true);
        return user;

    }

//     get the current time
    public static String getTime(){
        DateFormat df = new SimpleDateFormat("MM/dd/yy H:mm a");
        Date dateobj = new Date();
        String time = df.format(dateobj);
        return time;
    }

//    check whether the userList is full, if not, add the user to it
//    otherwise send a message back to client.
    public synchronized static boolean addUser(User currentUser, PrintWriter out) {

        boolean added = false;
        if (userList.size() < 2) {

            userList.add(currentUser);
            added = true;

        } else {

            out.println("full");
        }

        return added;

    }

//    check whether a user's messageList is full, if it is not full, add a message to it
//    if it is full, send a message back to the client
    public synchronized static boolean storeMessage(User user, String message, PrintWriter out) {

        if (user.messageList.size() < 2) {

            user.setMessage(message);
            return true;

        } else {

            out.println("full");
            return false;

        }

    }

//    check userList to find the user with the same name provided.
    public static int checkAUserStatus(String name) {

        int index = -1;

        for(int i = 0; i < userList.size(); i++) {

            if(userList.get(i).name.equals(name)) {
                index = i;
                break;
            }

        }

        return index;

    }

//    check one of the user from the userList to see whether it is connected.
    public static boolean checkAUserConnection(int index) {

        boolean connection = false;

        if (userList.get(index).connected) {

            connection = true;

        }

        return connection;

    }


//    Check userList to find all known users. Then if the client chose "1", just print out all the known user names
//    If the client chose "5", save a message to all known users.
    public void checkAllKnownUsers (String choice, String message) {

        int i = 0;
        while(i < userList.size()) {

            if (choice.equals("1")) {

                out.println(userList.get(i).name);

            } else if (choice.equals("5")){

                if (!userList.get(i).name.equals(newUser.name)) {

                    boolean saved = storeMessage(userList.get(i), message, out);
                    if(!saved) {

                        out.println(userList.get(i) + "'s message is full and can't be stored");

                    }

                }

            }

            i++;

        }

        if(choice.equals("1")) {

            System.out.println(getTime() + ", " + newUser.name + " displays all known users.");

        } else if (choice.equals("5")){

            System.out.println(getTime() + ", " + newUser.name + " posts a message for all known users.");
            out.println("Message posted to all known users.");

        }


    }

//    check userList to fined all connected users. Then if the client chose option "2", print out all connected user names
//    If the client chose "4", add a message to all connected users.
    public void checkAllConnectedUsers(String choice, String message) {

        int i = 0;
        while(i < userList.size()) {

            if(userList.get(i).connected) {

                if(choice.equals("2")) {

                    out.println(userList.get(i).name);

                } else if (choice.equals("4")){

                    if (!userList.get(i).name.equals(newUser.name)) {

                        String time = getTime();
                        String sendMessage = newUser.name + "\n" + time + "\n" + message;
                        boolean saved = storeMessage(userList.get(i), sendMessage, out);
                        if(!saved) {

                            out.println(userList.get(i) + "'s message is full and can't be stored.");

                        }

                    }


                }

            }

            i++;

        }

        if(choice.equals("2")) {

            System.out.println(getTime() + ", " + newUser.name + " displays all connected users.");

        } else if (choice.equals("4")){

            System.out.println(getTime() + ", " + newUser.name + " posts a message for all currently connected users.");
            out.println("Message posted to all currently connected users.");

        }

    }

//    Check whether it's a new user, if it is, add him to the user List, otherwise get the
//    user from the user list.
    public void updateUser (int index, String name, String message) {

        User user = new User();
        if (index == -1) {

            boolean added = false;
            user.setName(name);
            user.setStatus(status);
            added = addUser(user, out);
            if (added) {

                saveOneUserMessage(user, message);

            }

        } else {

            user = userList.get(index);
            saveOneUserMessage(user, message);

        }

    }

    public void saveOneUserMessage (User user, String sendMessage){

        boolean saved = storeMessage(user, sendMessage, out);

        if (saved) {

            out.println("Message posted to " + user.name);

        } else {

            out.println(coUser.name + "'s message is full and can't be saved");

        }

        System.out.println(getTime() + ", " + newUser.name + " posts a message for " + coUser.name + ".");
    }

//    close the client socket.
    public void exitClient() {
        try
        {
            client.close();
        }
        catch (IOException e)
        {
            System.out.println("Close failed");
            System.exit(-1);
        }
    }

}
