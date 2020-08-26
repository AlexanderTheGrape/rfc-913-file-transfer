package test;

import main.client.TCPClient;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TCPClientTest {
    private TCPClient tcpClient = new TCPClient();

    @Test
    public void testClientFileSend(){
        // TODO read what I should check for in my first test
        assertEquals("something", "also something");
    }

}
