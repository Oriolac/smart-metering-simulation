package udl.cig.sms.connection;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConnectionMeterTest {

    @Test
    void tomlToAddressTest() throws IOException {
        new ServerSocket(5000, 1, InetAddress.getByName("localhost"));
        assertNotNull(ConnectionMeter.tomlToSocket(new File("./data/test/substation.toml")));
    }

    @Test
    void receive() {
    }

    @Test
    void send() {
    }
}