package main.server;

import java.io.*;
import java.net.*;

public class TCPServer{
    public static void main(String[] args) throws Exception{
        String clientSentence;
        String capitalizedSentence;

        // Create welcoming socket at port 6789
        ServerSocket welcomeSocket = new ServerSocket(6789);

        System.out.println("Server running");

        while(true){
            // Wait on welcoming socket for contact by a client
            Socket connectionSocket = welcomeSocket.accept();

            // Create input stream, attached to socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            // Create output stream, attached to socket
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // Read in line from socket
            clientSentence = inFromClient.readLine();
            capitalizedSentence = clientSentence.toUpperCase();

            // Write out line to socket
            outToClient.writeBytes(capitalizedSentence);

            // End of while loop, loop back and wait for another client connection
        }

    }
}
