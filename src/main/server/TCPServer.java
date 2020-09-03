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
            //outputLine = kkp.processInput(null);
            outputLine = kkp.generateResponse("");
            out.println(outputLine);

//          while ((inputLine = in.readLine()) != null) {
            while (outputLine != "+Session closed") {
                inputLine = in.readLine();
                //outputLine = kkp.processInput(inputLine);
                outputLine = kkp.generateResponse(inputLine);
                out.println(outputLine);
                if (kkp.getState() != 15){ // bug in CDIR TODO figure out why CDIR completes prematurely
                    continue;
                }
                if (outputLine.equals("+Session closed"))
                    serverSocket.close();
                    break;
            }
            System.out.println("End of program");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }


}
