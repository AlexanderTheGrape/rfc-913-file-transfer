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
        rfcProtocol rfcProtocol = new rfcProtocol();
        String stringFromClient = "USER \0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("-Invalid user-id, try again", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringNoPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();
        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();
        String stringFromClient = "USER user456\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("+User-id valid, send account and password", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithNoArgs(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user456\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT \0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("-Invalid account, try again", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithArgAndNotRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user456\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct1\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("! Account valid, logged-in", responseText);
    }

    @Test
    public void testGenerateResponseToACCTCommandWithArgAndRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("+Account valid, send password", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithNoArgs(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "PASS \0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("-Wrong password, try again", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithArgsAndAccountConfirmed(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "PASS pass1\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("! Logged in", responseText);
    }

    @Test
    public void testGenerateResponseToPASSCommandWithArgsAndAccountNotYetConfirmed(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "PASS pass1\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("+Send account", responseText);
    }

    @Test
    public void testGenerateResponseToLISTCommandWhenNotLoggedIn(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "LIST F\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals(
                "-ERROR: Not logged in",
                responseText);
    }

    @Test
    public void testGenerateResponseToLISTCommandWithFArg(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "LIST F\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals(
                "+C:\\Users\\Alex\\Documents\\Repos\\rfc-913-file-transfer\n" +
                        ".git\n" +
                        ".idea\n" +
                        ".travis.yml\n" +
                        "deleteMe2.txt\n" +
                        "existingFileName.txt\n" +
                        "filename.txt\n" +
                        "images\n" +
                        "loginDetails.txt\n" +
                        "newFileName.txt\n" +
                        "out\n" +
                        "README.md\n" +
                        "renameMe.txt\n" +
                        "rfc-913-file-transfer.iml\n" +
                        "src\n",
                responseText);
    }

    @Test
    public void testGenerateResponseToLISTCommandWithVerboseArg(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);


        stringFromClient = "LIST V\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        // EXPECT SOMETHING LIKE THIS, BUT WITH SIZE IN BYTES AND IF EACH FILE IS WRITEABLE
        assertEquals(
                "+C:\\Users\\Alex\\Documents\\Repos\\rfc-913-file-transfer\n" +
                        ".git\n" +
                        ".idea\n" +
                        ".travis.yml\n" +
                        "deleteMe2.txt\n" +
                        "existingFileName.txt\n" +
                        "filename.txt\n" +
                        "images\n" +
                        "loginDetails.txt\n" +
                        "newFileName.txt\n" +
                        "out\n" +
                        "README.md\n" +
                        "renameMe.txt\n" +
                        "rfc-913-file-transfer.iml\n" +
                        "src\n",
                responseText);
    }

    @Test
    public void testGenerateResponseToKILLCommandWhenNotLoggedIn(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        File myObj = new File("deleteMe.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        String stringFromClient = "KILL deleteMe.txt\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-ERROR: Not logged in", responseText);
    }

    @Test
    public void testGenerateResponseToKILLCommandWithCorrectFileNameWhenLoggedIn(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        File myObj = new File("deleteMe.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        stringFromClient = "KILL deleteMe.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+deleteMe.txt deleted", responseText);
    }

    @Test
    public void testGenerateResponseToKILLCommandWithInvalidFileNameWhenLoggedIn(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        File myObj = new File("deleteMe.txt");
        try{
            myObj.createNewFile();
        } catch (Exception e){

        }

        stringFromClient = "KILL deleteMe.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        // After deletion, we cannot delete the same again
        stringFromClient = "KILL deleteMe.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("-Not deleted because file does not exist", responseText);
    }

    @Test
    public void testGenerateResponseToNAMECommandWhenNotLoggedIn(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "NAME validFile.txt\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-ERROR: Not logged in", responseText);
    }

    @Test
    public void testGenerateResponseToNAMECommandWithInvalidFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "NAME notAValidFile.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-Can't find notAValidFile.txt", responseText);
    }

    @Test
    public void testGenerateResponseToNAMECommandWithValidFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

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

        stringFromClient = "NAME filename.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+File exists", responseText);

        stringFromClient = "TOBE newFileName.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+filename.txt renamed to newFileName.txt", responseText);
    }

    @Test
    public void testGenerateResponseToNAMECommandWithInvalidNewFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

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

        stringFromClient = "NAME filename.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+File exists", responseText);

        stringFromClient = "TOBE existingFileName.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-File wasn't renamed because a file with the new name already exists", responseText);
    }

    @Test
    public void testGenerateResponseToDONECommandWhenNotLoggedIn(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "DONE \0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-ERROR: Not logged in", responseText);
    }

    @Test
    public void testGenerateResponseToDONECommand(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "DONE \0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+Session closed", responseText);
    }

    @Test
    public void testGenerateResponseToCDIRCommandRequiringACCTAndPASS(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user789\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "ACCT acct2\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        stringFromClient = "PASS pass1\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("! Logged in", responseText);

        stringFromClient = "CDIR \\src\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+directory ok, send account/password", responseText);

        stringFromClient = "ACCT acct2\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("+account ok, send password", responseText);

        stringFromClient = "PASS pass1\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("!Changed working dir to \\src", responseText);
    }

    @Test
    public void testGenerateResponseToCDIRCommandRequiringNoAdditionalCredentials(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "CDIR \\images\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("!Changed working dir to \\images", responseText);
    }

    @Test
    public void testGenerateResponseToRETRCommandWithInvalidFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "RETR \\invalidFile.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("-File doesn't exist", responseText);
    }

    @Test
    public void testGenerateResponseToRETRCommandWithValidFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "RETR \\loginDetails.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("43", responseText); // <number-of-bytes-that-will-be-sent> -> 43 in this case
    }

    @Test
    public void testGenerateResponseToSENDCommandWithValidFileName(){
        rfcProtocol rfcProtocol = new rfcProtocol();

        String stringFromClient = "USER user123\0";
        String responseText = rfcProtocol.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);

        stringFromClient = "RETR \\loginDetails.txt\0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        assertEquals("43", responseText); // <number-of-bytes-that-will-be-sent> -> 43 in this case

        stringFromClient = "SEND \0";
        responseText = rfcProtocol.generateResponse(stringFromClient);

        // ----------- NOTE: this is a set of 43 bytes sent separately, with each character being a byte,
        //                  and will not pass this test. It is below for manual testing purposes (so you
        //                  can see what the bytes should look like).
        assertEquals("dXNlcjEyMw0KdXNlcjQ1NiBhY2N0MQ0KdXNlcjc4OSBhY2N0MiBwYXNzMQ==", responseText);

    }

}
