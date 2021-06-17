package cat.udl.cig.sms.data;

import cat.udl.cig.sms.connection.SocketReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocketReaderTest {

    private static File file;
    private static File file2;

    @BeforeAll
    static void setUp() {
        file = new File("./data/test/substation1.toml");
        file2 = new File("./data/test/substation2.toml");
    }

    @Test
    void tomlToServerSocket() throws IOException {
        assertNotNull(SocketReader.tomlToServerSocket(file2));
    }

    @Test
    void getNumberOfMeters() throws IOException {
        assertEquals(3, SocketReader.getNumberOfMeters(file));
    }
}