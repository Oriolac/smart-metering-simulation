package udl.cig.sms.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static udl.cig.sms.data.LoadSocket.tomlToSocket;

class LoadSocketTest {

    private static File file;
    private static File file2;

    @BeforeAll
    static void setUp() {
        file = new File("./data/test/substation1.toml");
        file2 = new File("./data/test/substation2.toml");
    }

    @Test
    void tomlToServerSocket() throws IOException {
        assertNotNull(LoadSocket.tomlToServerSocket(file2));
    }

    @Test
    void getNumberOfMeters() throws IOException {
        assertEquals(3, LoadSocket.getNumberOfMeters(file));
    }
}