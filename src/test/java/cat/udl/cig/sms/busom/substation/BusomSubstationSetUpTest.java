package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.connection.ReceiverSubstation;
import cat.udl.cig.sms.connection.Sender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.certificate.CertificateTrueMock;
import cat.udl.cig.sms.busom.meter.doubles.SenderSpy;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusomSubstationSetUpTest {

    BusomSubstationSetUp substationSetup;
    CurveConfiguration curveConfiguration;
    ConnectionSubstationSpy connectionSubstationSpy;
    SubstationBusomContextInt substationBusomContextInt;
    ReceiverSpy receiverSpy;
    SenderSpy senderSpy;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        substationBusomContextInt = Mockito.mock(SubstationBusomContextInt.class);
        receiverSpy = new ReceiverSpy(curveConfiguration.getGroup().getGenerator());
        senderSpy = new SenderSpy();
        connectionSubstationSpy = new ConnectionSubstationSpy(receiverSpy, senderSpy);
        Mockito.when(substationBusomContextInt.getConnection()).thenReturn(connectionSubstationSpy);
        Mockito.when(substationBusomContextInt.getCertificateValidation()).thenReturn(new CertificateTrueMock<>());
        Mockito.when(substationBusomContextInt.makeReceiveChunk(Mockito.any())).then( c ->new ReceiveChunk(c.getArgument(0), substationBusomContextInt));
        substationSetup = new BusomSubstationSetUp(curveConfiguration.getGroup(), substationBusomContextInt);
    }

    @Test
    void next() throws IOException {
        BusomState nextState = substationSetup.next();
        assertNotNull(nextState);
        assertTrue(nextState instanceof ReceiveChunk, "nextState is " + nextState.getClass());
    }

    @Test
    void receiveAndComputePublicKey() throws IOException {
        substationSetup.receivePublicKeys();
        assertEquals(1, connectionSubstationSpy.getReceiverSpy().getCount());
    }

    @Test
    void sendPublicKey() throws IOException {
        int numberOfNHDatagrams = 3;
        List<NeighborhoodDatagram<String>> datagrams = new ArrayList<>();
        for (int i = 0; i < numberOfNHDatagrams; i++)
            datagrams.add(new NeighborhoodDatagram<>(curveConfiguration.getGroup().getGenerator(), ""));
        substationSetup.setDatagrams(datagrams);
        substationSetup.sendPublicKey();
        assertEquals(numberOfNHDatagrams + 1, connectionSubstationSpy.getSender().getCount());
    }

    static class ReceiverSpy implements ReceiverSubstation {

        int count;
        GroupElement generator;

        public ReceiverSpy(GroupElement generator) {
            this.count = 0;
            this.generator = generator;
        }

        @Override
        public List<SMSDatagram> receive() {
            count++;
            List<SMSDatagram> datas = new ArrayList<>();
            for(int i = 0; i < 3; i++)
                datas.add(new NeighborhoodDatagram<>(generator, ""));
            return datas;
        }

        public int getCount() {
            return count;
        }
    }

    static class ConnectionSubstationSpy implements ConnectionSubstationInt {
        private final ReceiverSpy receiverSpy;
        private final SenderSpy sender;

        public ConnectionSubstationSpy(ReceiverSpy receiverSpy, SenderSpy sender) {
            this.receiverSpy = receiverSpy;
            this.sender = sender;
        }

        @Override
        public int getNumberOfMeters() {
            throw new IllegalStateException();
        }

        @Override
        public List<SMSDatagram> receive() throws IOException {
            return receiverSpy.receive();
        }

        @Override
        public void send(SMSDatagram data) throws IOException {
            sender.send(data);
        }

        public ReceiverSpy getReceiverSpy() {
            return receiverSpy;
        }

        public SenderSpy getSender() {
            return sender;
        }

    }



}