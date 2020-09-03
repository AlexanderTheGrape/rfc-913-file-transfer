package test;

import main.client.TCPClient;
import main.rfcProtocol;
import main.server.TCPServer;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TCPClientTest {
    private TCPClient tcpClient = new TCPClient();

    // Client tests

    @Test
    public void testGenerateUSERCommandTextWithNoUserID(){
        // Checks that the data from the client is correctly formatted for USER command before
        // request is sent
        // Format: USER [<space> args] <Null>

        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateUSERCommandText();

        assertEquals("USER \0", commandText);
    }

    @Test
    public void testGenerateUSERCommandTextWithUserID(){
        // Checks that the data from the client is correctly formatted for USER command before
        // request is sent
        // Format: USER [<space> args] <Null>

        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateUSERCommandText("user1234");

        assertEquals("USER user1234\0", commandText);
    }

    @Test
    public void testGenerateACCTCommandTextWithNoAccount(){
        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateACCTCommandText();
        assertEquals("ACCT \0", commandText);
    }

    @Test
    public void testGenerateACCTCommandTextWithAccount(){
        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateACCTCommandText("acct1");
        assertEquals("ACCT acct1\0", commandText);
    }

    @Test
    public void testGeneratePASSCommandTextWithPassword(){
        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generatePASSCommandText("pass1");
        assertEquals("PASS pass1\0", commandText);
    }

    @Test
    public void testGenerateLISTCommandWithCorrectArgs(){
        // LIST { F | V } directory-path
        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateCommandText("LIST F PS: <MKL>");
        assertEquals("LIST F PS: <MKL>\0", commandText);
    }

//    @Test
//    public void testUSERIntegrationWithNoUserID(){
//        TCPServer tcpServer = new TCPServer();
//        TCPClient tcpClient = new TCPClient();
//
//        String stringFromUser = "USER ";
//        String commandText = tcpClient.generateCommandText(stringFromUser);
//        String responseTextFromServer = tcpClient.send(commandText);
//
//
//        assertEquals("-Invalid user-id, try again", responseTextFromServer);
//    }

    @Test
    public void testWholeSystemUSERCommand(){
        TCPServer tcpServer = new TCPServer();
        TCPClient tcpClient = new TCPClient();
        tcpClient.setMode(2); // programmed input

        tcpClient.sendProgrammedCommand("USER \0");
        String responseText = tcpClient.getFromServer();
        assertEquals("-Invalid user-id, try again", responseText);
    }

}
