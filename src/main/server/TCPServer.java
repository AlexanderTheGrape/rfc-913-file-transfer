package main.server;

import com.sun.security.ntlm.Server;
import main.rfcProtocol;
import main.util.FileReader;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer{

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            String inputLine, outputLine;

            // Initiate conversation with client
            rfcProtocol kkp = new rfcProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public String generateResponse(String stringFromClient){
        String[] splitString = stringFromClient.split(" ");
        String command = splitString[0];
        String args = splitString[1];
        args = args.replace("\0", "");

        switch (command){
            case "USER":
                return generateUSERResponse(args);
            case "ACCT":
                return generateACCTResponse(args);
            default:
                return "I don't know what command that is!";
        }

    }

    public String generateUSERResponse(String arg){
        // Check if the user-id is correct, by being in the list of user-id's in the txt file
        // Read lines from file, store in list
        ArrayList fileLines = new ArrayList();
        FileReader.readLines("loginDetails.txt", fileLines);

        for(int i = 0; i < fileLines.size(); i++){
            String line  = fileLines.get(i).toString();
            String[] splitString = line.split(" ");

            String username = splitString[0];

            if (username.equals(arg)){
                if (splitString.length > 1){
                    // This username has an associated account and password
                    // Further requests will need to be made to log in (ACCT, PASS)
                    return "+User-id valid, send account and password";

                } else {
                    // no password required
                    return String.format("!%s logged in", username);
                }
            }
        }
        return "-Invalid user-id, try again";
    }

    public String generateACCTResponse(String arg){
        //Check if there is an account for the given username
        // Read lines from file, store in list
        ArrayList fileLines = new ArrayList();
        FileReader.readLines("loginDetails.txt", fileLines);

        for(int i = 0; i < fileLines.size(); i++){
            String line  = fileLines.get(i).toString();
            String[] splitString = line.split(" ");

            String account = null;
            if (splitString.length > 1){
                account = splitString[1];
                if (account.equals(arg)){
                    if (splitString.length > 2){
                        // This username has an associated password
                        // Further requests will need to be made to log in (PASS)
                        return "+Account valid, send password";

                    } else {
                        // no password required
                        return "! Account valid, logged-in";
                    }
                }
            }
        }
        return "-Invalid account, try again";
    }
}
