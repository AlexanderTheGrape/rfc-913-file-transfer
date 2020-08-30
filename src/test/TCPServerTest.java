package test;

import main.server.TCPServer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TCPServerTest {

    @Test
    public void testGenerateResponseToUSERCommandWithNoUserID(){
        TCPServer tcpServer = new TCPServer();

        String stringFromClient = "USER \0";

        String responseText = tcpServer.generateResponse(stringFromClient);

        assertEquals("-Invalid user-id, try again", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringNoPassword(){
        TCPServer tcpServer = new TCPServer();
        String stringFromClient = "USER user123\0";
        String responseText = tcpServer.generateResponse(stringFromClient);
        assertEquals("!user123 logged in", responseText);
    }

    @Test
    public void testGenerateResponseToUSERCommandWithUserIDRequiringPassword(){
        TCPServer tcpServer = new TCPServer();
        String stringFromClient = "USER user456\0";
        String responseText = tcpServer.generateResponse(stringFromClient);
        assertEquals("+User-id valid, send account and password", responseText);
    }
}
