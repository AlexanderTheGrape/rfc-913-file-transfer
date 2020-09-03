package main;

import main.util.FileReader;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.String.valueOf;

public class rfcProtocol {
    private String currentUsername = null;
    private String currentAccount = null;
    private String currentPassword = null;
    private Boolean currentlyLoggedIn = false;
    private String ExistingOldFileSpec = "";
    private String currentDirectory = System.getProperty("user.dir");
    private String newDirectoryToNavigate = null;
    private int fileSize;
    private byte[] bytes;
    private int currentByte;

    private static final int WAITING = 0;
    private static final int READY = 1;

    private static final int RENAMING =  10;
    private static final int DEFAULT =  11;
    private static final int CDIRState = 12;
    private static final int RETR = 13;
    private static final int STOP = 14;
    private static final int DONE = 15;
    private static final int SEND = 16;

    private int state = WAITING;

    public int getState(){
        return state;
    }

    public String generateResponse(String stringFromClient){
        if (stringFromClient == ""){
            return "Server connected.";
        }

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
            case "LIST":
                return generateLISTResponse(args);
            case "KILL":
                return generateKILLResponse(args);
            case "TOBE":
                return generateTOBEResponse(args);
            case "NAME":
                return generateNAMEResponse(args);
            case "DONE":
                return generateDoneResponse(args);
            case "CDIR":
                return generateCDIRResponse(args);
            case "RETR":
                return generateRETRResponse(args);

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
                            if (state == CDIRState){
                                if (currentPassword != null){
                                    return "!Changed working dir to " + newDirectoryToNavigate;
                                } else {
                                    return "+account ok, send password";
                                }
                            } else {
                                return "+Account valid, send password";
                            }

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
                                if (state == CDIRState){
                                    if (currentAccount != null){
                                        return "!Changed working dir to " + newDirectoryToNavigate;
                                    } else {
                                        return "+password ok, send account";
                                    }
                                } else {
                                    return "! Logged in";
                                }
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

    public String generateLISTResponse(String format){
        // Format is "F" or "V"
        try {
            if (format.equals("F")) {
                // obtain the current directory
                String absPath = System.getProperty("user.dir");

                // Create a file object
                File f = new File(absPath);

                // Get all the names of the files present
                // in the given directory
                File[] files = f.listFiles();

                String outputString = "+" + absPath + "\r";
                if (files.length > 0){
                    for (int i = 0; i < files.length; i++){
                        outputString = outputString + "\n" + files[i].toString().replace(absPath + File.separator, "") + "\r";
                    }
                }
                outputString = outputString + "\0";
                return outputString;
            } else if (format.equals("V")) {
                // obtain the current directory
                String absPath = System.getProperty("user.dir");

                // Create a file object
                File f = new File(absPath);

                // Get all the names of the files present
                // in the given directory
                File[] files = f.listFiles();

                String outputString = "+" + absPath + "\r";
                if (files.length > 0){
                    for (int i = 0; i < files.length; i++){
                        // obtain file size, other details too
                        String fileSize = valueOf(files[i].length());
                        String lastModified = valueOf(files[i].lastModified());
                        String canWrite;
                        if (true ==files[i].canWrite()){
                            canWrite = "writeable";
                        } else {
                            canWrite = "non-writeable";
                        }
                        outputString = outputString + "\n" +
                                files[i].toString().replace(absPath + File.separator, "") +
                                "   file size: " + fileSize + "   " + canWrite +  "\r";

                    }
                }
                outputString = outputString + "\0";
                return outputString;
            }
        } catch(Exception e){
            return "-" + e;
        }
        return "-ERROR - F or V must be given";
    }

    public String generateKILLResponse(String fileSpec){
        try{
            //create file object from file
            String absPath = System.getProperty("user.dir");
            File f = new File(absPath + File.separator + fileSpec);

            try {
                Boolean exists = Files.deleteIfExists(f.toPath());
                if (exists) {
                    return "+" + fileSpec + " deleted";
                } else {
                    return "-Not deleted because file does not exist";
                }
            } catch (Exception e){
                return "-Not deleted because " + e.toString();
            }
        } catch(Exception e) {
            return "-Not deleted because " + e.toString();
        }
    }

    public String generateNAMEResponse(String oldFileSpec){
        // check if the specified file exists
        String absPath = System.getProperty("user.dir");
        File f = new File(absPath + File.separator + oldFileSpec);

        Boolean fileExists = Files.exists(f.toPath());
        if (fileExists) {
            state = RENAMING;
            this.ExistingOldFileSpec = oldFileSpec;
            return "+File exists";
        } else {
            return "-Can't find " + oldFileSpec;
        }

    }

    public String generateTOBEResponse(String newFileSpec){
        if (state == RENAMING) {
            File f = new File(currentDirectory + File.separator + ExistingOldFileSpec);

            // create new file object with new name
            File f2 = new File(currentDirectory + File.separator + newFileSpec);
            // check if file exists
            if (f2.exists()) {
                return "-File wasn\'t renamed because a file with the new name already exists";
            }

            // Rename file (or directory)
            boolean success = f.renameTo(f2);

            if (!success) {
                // File was not successfully renamed
                return "-File wasn\'t renamed because an error occurred when renaming";
            }
            return "+" + ExistingOldFileSpec + " renamed to " + newFileSpec;
        } else {
            return "-File wasn't renamed because no file to rename has been submitted with a NAME command";
        }
    }

    public String generateDoneResponse(String args){
        state = DONE;
        return "+Session closed";
    }

    public String generateCDIRResponse(String newDirectory){
        // check if this was the last state
        if (state != CDIRState){
            this.currentAccount = null;
            this.currentPassword = null;
        }

        // set currentState to this
        state = CDIRState;
        this.newDirectoryToNavigate = newDirectory;

        // check if directory is valid
        String absPath = System.getProperty("user.dir");
        Path path = Paths.get(absPath + newDirectory);
        Boolean directoryValid = Files.exists(path);

        if (directoryValid){
            Boolean validAccount = (null != currentAccount);
            Boolean validPassword = (null != currentPassword);

            // Assume that directories need no extra credentials, but the \src directory needs
            // both ACCT and PASS.
            if (!newDirectory.equals("\\src")){
                currentDirectory = currentDirectory + newDirectory;
                return "!Changed working dir to " + newDirectory;
            } else {
                if (validAccount && validPassword) {
                    currentDirectory = currentDirectory + newDirectory;
                    return "!Changed working dir to " + newDirectory;
                } else if (validAccount) {
                    return "+account ok, send password";
                } else if (validPassword) {
                    return "+password ok, send account";
                } else {
                    return "+directory ok, send account/password";
                }
            }
        } else {
            return "-Can't connect to directory because directory is not valid";
        }
    }

    public String generateRETRResponse(String fileSpecToSend){

        // create new file object with new name
        File f = new File(currentDirectory + File.separator + fileSpecToSend);
        // check if file exists
        if (f.exists()) {
            state = RETR;
            try {
                bytes =  Files.readAllBytes(f.toPath());
            } catch (Exception e){
                return e.toString();
            }

            fileSize = bytes.length;
            currentByte = 0;
            return valueOf(fileSize); //TODO check if this number is given in bytes or kb (it needs to be bytes)
        } else {
            return "-File doesn't exist";
        }
    }

    public String generateSENDResponse(){
        // TODO send all the bytes in an 8-bit stream, and needs to be concurrent
        state = SEND;
        while(state != STOP){
            return "";// 8 bits at a time
        }
        return "-Error";
    }

    public String generateSTOPResponse(){
        state = STOP;
        return "+ok, RETR aborted";
    }
}
