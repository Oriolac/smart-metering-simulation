package cat.udl.cig.sms.connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.connection.ConnectionMeter;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionMeterTest {


    private ConnectionMeter connectionMeter;
    private DataOutputStream outputWriter;

    @BeforeEach
    void setUp() throws IOException {
        LoadCurve loadCurve = LoadCurve.P192();
        InputStream input = Mockito.mock(InputStream.class);
        Mockito.when(input.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt()))
                .then((Answer<Integer>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return (int) args[2];
        });
        DataInputStream inputReader = new DataInputStream(input);
        outputWriter = Mockito.mock(DataOutputStream.class);
        connectionMeter = new ConnectionMeter(inputReader, outputWriter, loadCurve);
    }

    @Test
    void receive() throws IOException {
        SMSDatagram data = connectionMeter.receive();
        assertTrue(data instanceof CipherTextDatagram);
    }

    @Test
    void send() throws IOException {
        connectionMeter.send(new EndOfDatagram());
        Mockito.verify(outputWriter).write(Mockito.eq(new EndOfDatagram().toByteArray()));
    }
}