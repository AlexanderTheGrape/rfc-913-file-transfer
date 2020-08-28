package test;

import main.client.TCPClient;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TCPClientTest {
    private TCPClient tcpClient = new TCPClient();

    // Client tests

    @Test
    public void testgenerateUserCommandTextWithNoUserID(){
        // Checks that the data from the client is correctly formatted for USER command before
        // request is sent
        // Format: USER [<space> args] <Null>

        /* If the remote system does not have user-id's then you should
         send an identification such as your personal name or host name
         as the argument, and the remote system would reply with '+'.
        */
        TCPClient tcpClient = new TCPClient();

        String commandText = tcpClient.generateUserCommandText();

        assertEquals("USER " + "\0", commandText);
    }

}
