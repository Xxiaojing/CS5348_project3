import java.io.*;
import java.net.Socket;
import java.text.*;
import java.util.*;

public class ClientWorker implements Runnable {

    private Socket client;
    private static ArrayList<User> userList;
    private User newUser = new User();
    private User coUser = new User();
    String status = "known";
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

//                    check whether the person sending message to is a known user or not.
//                    If he's unknown, add him to the user list.
                    int index = checkAUserStatus(name);
                    updateUser(index, name);

                    String sendMessage = newUser.name + "\n" + time + "\n" + message;
                    storeMessage(coUser, sendMessage);

                    out.println("Message posted to " + name + "\n");
                    System.out.println(newUser.name + " posts a message for " + coUser.name + ".");

                } else if (line.equals("4")) {

                    String message = in.readLine();
                    checkAllConnectedUsers(line, message);
                    System.out.println(newUser.name + " posts a message for all currently connected users.");
                    out.println("Message posted to all currently connected users.");

                } else if (line.equals("5")) {

                    String message = in.readLine();
                    checkAllKnownUsers(line, message);

                } else if (line.equals("6")) {

                    int i = 0;
                    while (i < newUser.messageList.size()) {

                        out.println(newUser.messageList.get(i));
                        i++;

                    }

                    System.out.println(newUser.name + " gets messages.");

                } else if (line.equals("7")) {

                    newUser.setConnected(false);
                    System.out.println(newUser.name + " exits.");
                    exitClient();
                    break;

                }

                out.println("stop");

            }

        } catch (IOException e) {
            System.out.println("Read failed");
            System.exit(-1);
        }
    }

//        Check whether the name provide by the client is a valid name
//    and get a valid name eventually and add the user to the user list.
    public static synchronized User userConnectionCheck (BufferedReader in, PrintWriter out, User newUser, String status) {

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

                newUser.setName(line);
                addUser(newUser);
                out.println("valid");
                break;

            } else {

                if(!checkAUserConnection(index)) {

                    newUser = userList.get(index);
                    out.println("valid");
                    break;

                } else {

                    out.println("invalid");

                }

            }

        }

        System.out.println(getTime() + ", " + "Connection by " + newUser.status + " user " + line);
        newUser.setStatus(status);
        newUser.setConnected(true);
        return newUser;

    }


    public static String getTime(){
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm a");
        Date dateobj = new Date();
        String time = df.format(dateobj);
        return time;
    }

    public synchronized static void addUser(User currentUser) {

        userList.add(currentUser);

    }

    public synchronized static void storeMessage(User coUser, String message) {

        coUser.setMessage(message);

    }

    public static int checkAUserStatus(String name) {

        int index = -1;

        for(int i = 1; i < userList.size(); i++) {

            if(userList.get(i).name.equals(name)) {
                index = i;
                break;
            }

        }

        return index;

    }

    public static boolean checkAUserConnection(int index) {

        boolean connection = false;

        if (userList.get(index).connected) {

            connection = true;

        }

        return connection;

    }


    public void checkAllKnownUsers (String choice, String message) {

        int i = 0;
		String nameList = null;
        while(i < userList.size()) {

            if (choice.equals("1")) {

                nameList =+ userList.get(i).name + "\n";

            } else {

                    String time = getTime();
                    String sendMessage = newUser.name + "\n" + time + "\n" + message;
                    storeMessage(coUser, sendMessage);

            }

            i++;

        }
		out.println(nameList);

        if(choice.equals("1")) {

            System.out.println(newUser.name + " displays all known users.");

        } else {

            System.out.println(newUser.name + " posts a message for all known users.");
            out.println("Message posted to all known users.");

        }


    }

    public void checkAllConnectedUsers(String choice, String message) {

        int i = 0;
        while(i < userList.size()) {

            if(userList.get(i).connected) {

                if(choice.equals("2")) {

                    out.println(userList.get(i).name);

                } else {
                    
                        String time = getTime();
                        String sendMessage = newUser.name + "\n" + time + "\n" + message;
                        storeMessage(coUser, sendMessage);

                }

            }

            i++;

        }

        if(choice.equals("2")) {

            System.out.println(newUser.name + " displays all connected users.");

        } else {

        }

    }

//    Check whether it's a new user, if it, add him to the user List, otherwise get the
//    user from the user list.
    public void updateUser (int index, String line) {

        if (index == -1) {

            coUser.setName(line);
            coUser.setStatus(status);
            addUser(coUser);

        } else {

            coUser = userList.get(index);

        }

    }

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
