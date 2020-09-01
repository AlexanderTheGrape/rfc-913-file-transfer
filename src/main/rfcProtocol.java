package main;

import main.util.FileReader;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class rfcProtocol {
    private String currentUsername = null;
    private String currentAccount = null;
    private String currentPassword = null;
    private Boolean currentlyLoggedIn = false;


    private static final int WAITING = 0;
    private static final int SENTKNOCKKNOCK = 1;
    private static final int SENTCLUE = 2;
    private static final int ANOTHER = 3;

    private static final int NUMJOKES = 5;

    private int state = WAITING;
    private int currentJoke = 0;

    private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
    private String[] answers = { "Turnip the heat, it's cold in here!",
            "I didn't know you could yodel!",
            "Bless you!",
            "Is there an owl in here?",
            "Is there an echo in here?" };

    public String processInput(String theInput) {
        String theOutput = null;

        if (state == WAITING) {
            theOutput = "Knock! Knock!";
            state = SENTKNOCKKNOCK;
        } else if (state == SENTKNOCKKNOCK) {
            if (theInput.equalsIgnoreCase("Who's there?")) {
                theOutput = clues[currentJoke];
                state = SENTCLUE;
            } else {
                theOutput = "You're supposed to say \"Who's there?\"! " +
                        "Try again. Knock! Knock!";
            }
        } else if (state == SENTCLUE) {
            if (theInput.equalsIgnoreCase(clues[currentJoke] + " who?")) {
                theOutput = answers[currentJoke] + " Want another? (y/n)";
                state = ANOTHER;
            } else {
                theOutput = "You're supposed to say \"" +
                        clues[currentJoke] +
                        " who?\"" +
                        "! Try again. Knock! Knock!";
                state = SENTKNOCKKNOCK;
            }
        } else if (state == ANOTHER) {
            if (theInput.equalsIgnoreCase("y")) {
                theOutput = "Knock! Knock!";
                if (currentJoke == (NUMJOKES - 1))
                    currentJoke = 0;
                else
                    currentJoke++;
                state = SENTKNOCKKNOCK;
            } else {
                theOutput = "Bye.";
                state = WAITING;
            }
        }
        return theOutput;
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
            case "PASS":
                return generatePASSResponse(args);
            default:
                return "I don't know what command that is!";
        }

    }

    public String generateUSERResponse(String arg){
        // Check if the user-id is correct, by being in the list of user-id's in the txt file
        // Read lines from file, store in list
        ArrayList fileLines = new ArrayList();
        main.util.FileReader.readLines("loginDetails.txt", fileLines);

        for(int i = 0; i < fileLines.size(); i++){
            String line  = fileLines.get(i).toString();
            String[] splitString = line.split(" ");

            String username = splitString[0];

            if (username.equals(arg)){
                if (splitString.length > 1){
                    // This username has an associated account and password
                    // Further requests will need to be made to log in (ACCT, PASS)
                    this.currentUsername = username;
                    return "+User-id valid, send account and password";

                } else {
                    // no password required
                    this.currentUsername = username;
                    this.currentlyLoggedIn = true;
                    return String.format("!%s logged in", username);
                }
            }
        }
        return "-Invalid user-id, try again";
    }

    public String generateACCTResponse(String acctFromClient){
        //Check if there is an account for the given username
        // Read lines from file, store in list
        ArrayList fileLines = new ArrayList();
        main.util.FileReader.readLines("loginDetails.txt", fileLines);

        for(int i = 0; i < fileLines.size(); i++){
            String line  = fileLines.get(i).toString();
            String[] splitString = line.split(" ");

            String account;
            if (this.currentUsername.equals(splitString[0])) {
                if (splitString.length > 1) {
                    account = splitString[1];
                    if (account.equals(acctFromClient)) {
                        if (splitString.length > 2) {
                            // This username has an associated password
                            // Further requests will need to be made to log in (PASS)
                            this.currentAccount = account;
                            return "+Account valid, send password";

                        } else {
                            // no password required
                            this.currentAccount = account;
                            this.currentlyLoggedIn = true;
                            return "! Account valid, logged-in";
                        }
                    }
                }
            }
        }
        return "-Invalid account, try again";
    }

    public String generatePASSResponse(String passwordFromClient){
        //Check if there is an account for the given username
        // Read lines from file, store in list
        ArrayList fileLines = new ArrayList();
        FileReader.readLines("loginDetails.txt", fileLines);

        for(int i = 0; i < fileLines.size(); i++){
            String line  = fileLines.get(i).toString();
            String[] splitString = line.split(" ");

            String account;
            if (this.currentUsername.equals(splitString[0])) {
                if (splitString.length > 1) {
                    account = splitString[1];
                    if (splitString.length > 2) {
                        // This username has an associated password
                        if (passwordFromClient.equals(splitString[2])) {
                            String acc = this.currentAccount;
                            String accTxt = splitString[1];
                            if (accTxt.equals(acc)) {
                                this.currentlyLoggedIn = true;
                                return "! Logged in";
                            } else if (this.currentAccount == null){
                                this.currentPassword = passwordFromClient;
                                return "+Send account";
                            }
                        } else {
                            return "-Wrong password, try again";
                        }
                    }

                }
            }
        }
        return "-Invalid account, try again";
    }
}
