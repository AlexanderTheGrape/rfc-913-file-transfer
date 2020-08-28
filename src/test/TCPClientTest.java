package test;

import main.client.TCPClient;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TCPClientTest {
    private TCPClient tcpClient = new TCPClient();

    // Client tests

    @Test
    public void testGenerateUserCommandTextWithNoUserID(){
        // Checks that the data from the client is correctly formatted for USER command before
        // request is sent
        // Format: USER [<space> args] <Null>

        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateUserCommandText();

        assertEquals("USER \0", commandText);
    }

    @Test
    public void testGenerateUserCommandTextWithUserID(){
        // Checks that the data from the client is correctly formatted for USER command before
        // request is sent
        // Format: USER [<space> args] <Null>

        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateUserCommandText("user1234");

        assertEquals("USER user1234\0", commandText);
    }

}
