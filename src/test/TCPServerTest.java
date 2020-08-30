package test;

import main.server.TCPServer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TCPServerTest {

    @Test
    public void testGenerateResponseToUSERCommandWithNoUserID(){
        /*
         The reply to this command will be one of:

            !<user-id> logged in

               Meaning you don't need an account or password or you
               specified a user-id not needing them.

            +User-id valid, send account and password

            -Invalid user-id, try again

         If the remote system does not have user-id's then you should
         send an identification such as your personal name or host name
         as the argument, and the remote system would reply with '+'.
         */

        // Expected output: "-Invalid user-id, try again"
        TCPServer tcpServer = new TCPServer();

        String stringFromClient = "USER \0";

        String responseText = tcpServer.generateResponse(stringFromClient);

        assertEquals("-Invalid user-id, try again", responseText);

    }
}
