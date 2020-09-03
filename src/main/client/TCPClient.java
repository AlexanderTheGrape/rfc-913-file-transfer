package main.client;
import java.io.*;
import java.net.*;

public class TCPClient {
    private static final int userInput = 1;
    private static final int programmedInput = 2;
    private static String programmedInputString;
    private static String fromServer;

    private int MODE = 1;

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
//            String fromServer;
            String fromUser;

            int mode = userInput;

            while (true) {
                fromServer = in.readLine();
                if (fromServer == null){
                    break;
                }

                System.out.println("Server: " + fromServer);
                if (fromServer.equals("+Session closed")) {
                    kkSocket.close();
                    break;
                }

                if (mode == userInput){
                    //fromUser = stdIn.readLine();
                    fromUser = generateCommandText(stdIn.readLine());
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser);
                    }
                } else if (mode == programmedInput){
                    System.out.println("Client: " + programmedInputString);
                    out.println(programmedInputString);
                    if (programmedInputString.equals("DONE ")) {
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    public void setMode(int MODE){
        this.MODE = MODE;
    }

    public String getFromServer(){
        return fromServer;
    }

    public void sendProgrammedCommand(String userInput){
        String fromUser = generateCommandText(userInput);
        this.programmedInputString = fromUser;
    }

    public String generateUSERCommandText(String args){
        String commandText = "USER " + args + "\0";
        return commandText;
    }

    public String generateUSERCommandText(){
        return generateUSERCommandText("");
    }

    public String generateACCTCommandText(String args){
        String commandText = "ACCT " + args + "\0";
        return commandText;
    }

    public String generateACCTCommandText(){
        return generateACCTCommandText("");
    }

    public String generatePASSCommandText(String args){
        return "PASS " + args + "\0";
    }

    public static String generateCommandText(String args){
        return args + "\0";
    }
}
