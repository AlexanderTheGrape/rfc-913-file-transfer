package test;

import main.rfcProtocol;
import main.server.TCPServer;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class TCPServerTest {

    @Test
    public void testGenerateResponseToUSERCommandWithNoUserID(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();
        String stringFromClient = "USER \0";
        String responseText = kkp.generateResponse(stringFromClient);
        assertEquals("-Invalid user-id, try again", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringNoPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();
        String stringFromClient = "USER user123\0";
        String responseText = kkp.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();
        String stringFromClient = "USER user456\0";
        String responseText = kkp.generateResponse(stringFromClient);
        assertEquals("+User-id valid, send account and password", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithNoArgs(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user456\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "ACCT \0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("-Invalid account, try again", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithArgAndNotRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user456\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct1\0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("! Account valid, logged-in", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithArgAndRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("+Account valid, send password", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithNoArgs(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "PASS \0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("-Wrong password, try again", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithArgsAndAccountConfirmed(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "PASS pass1\0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("! Logged in", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithArgsAndAccountNotYetConfirmed(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = kkp.generateResponse(stringFromClient);

        stringFromClient = "PASS pass1\0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("+Send account", responseText);
    }

//    @Test
//    public void testGenerateResponseToLISTCommandWithFArg(){
//        TCPServer tcpServer = new TCPServer();
//        rfcProtocol kkp = new rfcProtocol();
//
//        String stringFromClient = "LIST F\0";
//        String responseText = kkp.generateResponse(stringFromClient);
//
//        assertEquals(
//                "+C:\\Users\\Alex\\Documents\\Repos\\rfc-913-file-transfer\n" +
//                ".git\n" +
//                ".idea\n" +
//                ".travis.yml\n" +
//                "loginDetails.txt\n" +
//                "out\n" +
//                "README.md\n" +
//                "rfc-913-file-transfer.iml\n" +
//                "src"+
//                "\0",
//                responseText);
//    }

//    @Test
//    public void testGenerateResponseToLISTCommandWithVArg(){
//        TCPServer tcpServer = new TCPServer();
//        rfcProtocol kkp = new rfcProtocol();
//
//        String stringFromClient = "LIST V\0";
//        String responseText = kkp.generateResponse(stringFromClient);
//
//        assertEquals(
//                "+C:\\Users\\Alex\\Documents\\Repos\\rfc-913-file-transfer\n" +
//                        ".git\n" +
//                        ".idea\n" +
//                        ".travis.yml 1kb\n" +
//                        "loginDetails.txt\n" +
//                        "out\n" +
//                        "README.md\n" +
//                        "rfc-913-file-transfer.iml\n" +
//                        "src"+
//                        "\0",
//                responseText);
//    }

    @Test
    public void testGenerateResponseToKILLCommandWithCorrectFileName(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        File myObj = new File("deleteMe.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        String stringFromClient = "KILL deleteMe.txt\0";
        String responseText = kkp.generateResponse(stringFromClient);

        assertEquals("+deleteMe.txt deleted", responseText);
    }

    @Test
    public void testGenerateResponseToKILLCommandWithInvalidFileName(){
        rfcProtocol kkp = new rfcProtocol();

        File myObj = new File("deleteMe.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        String stringFromClient = "KILL deleteMe.txt\0";
        String responseText = kkp.generateResponse(stringFromClient);

        // After deletion, we cannot delete the same again
        stringFromClient = "KILL deleteMe.txt\0";
        responseText = kkp.generateResponse(stringFromClient);
        assertEquals("-Not deleted because file does not exist", responseText);
    }

    @Test
    public void testGenerateResponseToRENAMECommandWithInvalidFileName(){
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "NAME notAValidFile.txt\0";
        String responseText = kkp.generateResponse(stringFromClient);

        assertEquals("-Can't find notAValidFile.txt", responseText);
    }

    @Test
    public void testGenerateResponseToRENAMECommandWithValidFileName(){
        rfcProtocol kkp = new rfcProtocol();

        File myObj = new File("filename.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        // Ensure the new file name doesn't exist
        String absPath = System.getProperty("user.dir");
        File f = new File(absPath + "\\" + "newFileName.txt");
        try{
            Files.deleteIfExists(f.toPath());
        } catch (Exception e){
        }

        String stringFromClient = "NAME filename.txt\0";
        String responseText = kkp.generateResponse(stringFromClient);

        assertEquals("+File exists", responseText);

        stringFromClient = "TOBE newFileName.txt\0";
        responseText = kkp.generateResponse(stringFromClient);

        assertEquals("+filename.txt renamed to newFileName.txt", responseText);
    }

    @Test
    public void testGenerateResponseToRENAMECommandWithInvalidNewFileName(){
        rfcProtocol kkp = new rfcProtocol();

        File myObj = new File("filename.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }
        File myObj2 = new File("existingFileName.txt");
        try{
            myObj2.createNewFile();
        } catch (Exception e){

        }

        String stringFromClient = "NAME filename.txt\0";
        String responseText = kkp.generateResponse(stringFromClient);

        assertEquals("+File exists", responseText);

        stringFromClient = "TOBE existingFileName.txt\0";
        responseText = kkp.generateResponse(stringFromClient);

        assertEquals("-File wasn't renamed because a file with the new name already exists", responseText);
    }


}
