package test;

import main.rfcProtocol;
import main.server.TCPServer;
import org.junit.Test;

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

    @Test
    public void testGenerateResponseToLISTCommandWithArgs(){
        TCPServer tcpServer = new TCPServer();
        rfcProtocol kkp = new rfcProtocol();

        String stringFromClient = "LIST F\0";
        String responseText = kkp.generateResponse(stringFromClient);

        assertEquals("+rfc-913-file-transfer\\src\\main\ncrfcProtocol.java", responseText);
    }
}
