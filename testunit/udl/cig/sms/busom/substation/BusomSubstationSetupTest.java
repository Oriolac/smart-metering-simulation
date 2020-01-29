package udl.cig.sms.busom.substation;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.certificate.CertificateTrueMock;
import udl.cig.sms.busom.certificate.CertificateValidation;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusomSubstationSetupTest {

    BusomSubstationSetup substationSetup;
    LoadCurve loadCurve;
    ReceiverSpy receiverSpy;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        substationSetup = new BusomSubstationSetup(loadCurve.getGroup());
        substationSetup.setValidation(new CertificateTrueMock<>());
        receiverSpy = new ReceiverSpy(loadCurve.getGroup().getGenerator());
        substationSetup.setReceiver(receiverSpy);
    }

    @Test
    void next() throws IOException {
        SenderSpy senderSpy = new SenderSpy();
        substationSetup.setSender(senderSpy);
        BusomState nextState = substationSetup.next();
        assertTrue(nextState instanceof ReceiveChunk);
    }

    @Test
    void receiveAndComputePublicKey() throws IOException {
        //TODO: Falta lo del Certificate
        substationSetup.receivePublicKeys();
        assertEquals(1, receiverSpy.getCount());
    }

    @Test
    void sendPublicKey() throws IOException {
        int numberOfDatagrams = 3;
        SenderSpy senderSpy = new SenderSpy();
        substationSetup.setSender(senderSpy);
        List<NeighborhoodDatagram<String>> datagrams = new ArrayList<>();
        for (int i = 0; i < numberOfDatagrams; i++)
            datagrams.add(new NeighborhoodDatagram<>(loadCurve.getGroup().getGenerator(), ""));
        substationSetup.setDatagrams(datagrams);
        substationSetup.sendPublicKey();
        assertEquals(numberOfDatagrams, senderSpy.getCount());
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

    static class CertificateValSpy<T> implements CertificateValidation<T> {

        int count;

        protected CertificateValSpy() {
            count = 0;
        }

        @Override
        public boolean validateCertificate(GroupElement element, T certificate) {
            count++;
            return true;
        }

        protected int getCount() {
            return count;
        }
    }

}