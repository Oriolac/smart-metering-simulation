package cat.udl.cig.sms.connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionMeterSubstation {

    private static ConnectionSubstation connectionSubstation;
    private static List<ConnectionMeter> connectionMeters;
    private static File fileSubst;
    private static CurveConfiguration curveConfiguration;

    @BeforeAll
    static void setUp() throws IOException {
        fileSubst = new File("./data/test/substation4.toml");
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));

        ServerSocket server = SocketReader.tomlToServerSocket(fileSubst);
        int numberOfMeters = SocketReader.getNumberOfMeters(fileSubst);
        connectionMeters = new ArrayList<>();
        for(int i = 0; i < numberOfMeters; i++)
            connectionMeters.add(new ConnectionMeter(fileSubst, curveConfiguration));
        connectionSubstation = new ConnectionSubstation(server, numberOfMeters, curveConfiguration);
    }

    @Test
    void IOExceptionTest() {
        assertThrows(IOException.class, () -> new ConnectionSubstation(fileSubst, curveConfiguration));
    }

    @Test
    void metersToSubstation() throws IOException {
        for (ConnectionMeter connectionMeter : connectionMeters) {
            connectionMeter.send(new EndOfDatagram());
        }
        List<SMSDatagram> datas = connectionSubstation.receive();
        for (SMSDatagram data : datas)
            assertTrue(data instanceof EndOfDatagram);
    }

    @Test
    void substationToMeters() throws IOException {
        connectionSubstation.send(new EndOfDatagram());
        for (ConnectionMeter connectionMeter : connectionMeters) {
            SMSDatagram data = connectionMeter.receive();
            assertTrue(data instanceof EndOfDatagram);
        }
    }

}