package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.sms.busom.BusomSubstationState;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.meter.doubles.SenderSpy;
import cat.udl.cig.sms.connection.ReceiverSubstation;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReceiveChunkTest {

    CurveConfiguration curveConfiguration;
    SubstationBusomContextInt substationBusomContextInt;
    ReceiveChunk currentState;
    ConnectionSpy connectionSpy;
    ReceiverSpy receiver;
    SenderSpy senderSpy;
    private ElGamalCiphertext cipherText;

    @BeforeEach
    void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        receiver = new ReceiverSpy(curveConfiguration);
        senderSpy = new SenderSpy();
        cipherText = new ElGamalCiphertext(receiver.getPoints("6209844826947604790038975056267208146258025268519512417642",
                "2994524308329009013576298176683420492475513562601889388197", "3322243116407084751555570864568066673271311194669141092600",
                "3196076688455990248772493899225500830789111078873298446819"));
        substationBusomContextInt = Mockito.mock(SubstationBusomContextInt.class);
        connectionSpy = new ConnectionSpy(receiver, senderSpy);
        Mockito.when(substationBusomContextInt.getConnection()).thenReturn(connectionSpy);
        Mockito.when(substationBusomContextInt.makeDecriptChunk(Mockito.any(), Mockito.any())).then((Answer<DecriptChunk>) invocationOnMock -> new DecriptChunk(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1) , substationBusomContextInt));
        currentState = new ReceiveChunk(curveConfiguration.getGroup(), substationBusomContextInt);
    }

    @Test
    void next() throws IOException {
        BusomSubstationState nextState = currentState.next();
        assertNotNull(nextState);
    }

    @Test
    void receiveAndCompute() {
        currentState.receiveAndCompute();
        assertEquals(1, receiver.getCount());
        ElGamalCiphertext ciphertext = (ElGamalCiphertext) currentState.getCiphertext();
        ElGamalCiphertext expected = ciphertext;
        assertEquals(expected, ciphertext);
    }

    @Test
    void sendC() throws IOException {
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(receiver.getPoints("6209844826947604790038975056267208146258025268519512417642",
                "2994524308329009013576298176683420492475513562601889388197", "3322243116407084751555570864568066673271311194669141092600",
                "3196076688455990248772493899225500830789111078873298446819"));
        currentState.setCipherText(ciphertext);
        currentState.sendC();
        assertEquals(1, senderSpy.getCount());
    }

    public static class ConnectionSpy implements ConnectionSubstationInt {

        private final ReceiverSpy receiverSpy;
        private final SenderSpy senderSpy;

        protected ConnectionSpy(ReceiverSpy receiverSpy, SenderSpy senderSpy) {
            this.receiverSpy = receiverSpy;
            this.senderSpy = senderSpy;
        }

        @Override
        public int getNumberOfMeters() {
            throw new IllegalStateException();
        }

        @Override
        public List<SMSDatagram> receive() throws IOException {
            return this.receiverSpy.receive();
        }

        @Override
        public void send(SMSDatagram data) throws IOException {
            this.senderSpy.send(data);
        }
    }

    public static class ReceiverSpy implements ReceiverSubstation {


        private final PrimeField field;
        private final GeneralEC curve;
        private final ECPrimeOrderSubgroup group;
        private int count;
        private HashMap<Integer, HomomorphicCiphertext> ciphertexts;

        protected ReceiverSpy(CurveConfiguration curveConfiguration) {
            this.count = 0;
            ciphertexts = new HashMap<>();
            curve = curveConfiguration.getCurve();
            group = curveConfiguration.getGroup();
            field = curveConfiguration.getField();
            addCipherTexts();
        }

        private void addCipherTexts() {
            HomomorphicCiphertext cipher = new ElGamalCiphertext(
                    getPoints("3473339081378406123852871299395262476289672479707038350589",
                            "2152713176906603604200842901176476029776544337891569565621",
                            "4373825320106791088761300818935615714553543989612531409678",
                            "3361246185366078556298692947762214183297736839626498588973"));
            ciphertexts.put(1, cipher);
            cipher = new ElGamalCiphertext(
                    getPoints("1167950611014894512313033362696697441497340081390841490910",
                            "4002177906111215127148483369584652296488769677804145538752",
                            "5467507639542780949901857448567478910725576155900792511478",
                            "1563849217525333779222680090890148857404203243691036054997"));
            ciphertexts.put(2, cipher);
            cipher = new ElGamalCiphertext(
                    getPoints("3176317450453705650283775811228493626776489433309636475023",
                            "44601893774669384766793803854980115179612118075017062201",
                            "2950417114176809050594387653481945334849577973154156222810",
                            "2516874068528847001462285716637906199919765877247090275117"));
            ciphertexts.put(3, cipher);
        }

        public GroupElement[] getPoints(String s1, String s2, String s3, String s4) {
            List<GroupElement> elements = new ArrayList<>();
            elements.add(getPoint(s1, s2));
            elements.add(getPoint(s3, s4));
            return elements.toArray(new GroupElement[0]);
        }

        public GroupElement getPoint(String s1, String s2) {
            return new GeneralECPoint(curve,
                    field.toElement(new BigInteger(s1)), field.toElement(new BigInteger(s2)));
        }

        @Override
        public List<SMSDatagram> receive() {
            count++;
            return ciphertexts.values().stream().map(CipherTextDatagram::new).collect(Collectors.toList());
        }

        public int getCount() {
            return count;
        }
    }
}