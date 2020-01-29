package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.PrimeField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiveChunkTest {

    LoadCurve loadCurve;
    ReceiveChunk currentState;
    ReceiverSpy receiver;
    SenderSpy senderSpy;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        currentState = new ReceiveChunk(loadCurve.getGroup());
        receiver = new ReceiverSpy(loadCurve);
        currentState.setReceiver(receiver);
        senderSpy = new SenderSpy();
        currentState.setSender(senderSpy);
    }

    @Test
    void next() throws IOException {
        BusomState nextState = currentState.next();
        assertTrue(nextState instanceof DecriptChunk);
    }

    @Test
    void receiveAndCompute() {
        currentState.receiveAndCompute();
        assertEquals(1, receiver.getCount());
        ElGamalCiphertext ciphertext = (ElGamalCiphertext) currentState.getCiphertext();
        ElGamalCiphertext expected = new ElGamalCiphertext(receiver.getPoints("6209844826947604790038975056267208146258025268519512417642",
                "2994524308329009013576298176683420492475513562601889388197", "3322243116407084751555570864568066673271311194669141092600",
                "3196076688455990248772493899225500830789111078873298446819"));
        assertEquals(expected, ciphertext);
    }

    @Test
    void sendC() throws IOException {
        currentState.setSender(senderSpy);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(receiver.getPoints("6209844826947604790038975056267208146258025268519512417642",
                "2994524308329009013576298176683420492475513562601889388197", "3322243116407084751555570864568066673271311194669141092600",
                "3196076688455990248772493899225500830789111078873298446819"));
        currentState.setCipherText(ciphertext);
        currentState.sendC();
        assertEquals(1, senderSpy.getCount());
    }

    protected static class ReceiverSpy implements ReceiverSubstation {


        private final PrimeField field;
        private final GeneralEC curve;
        private final ECPrimeOrderSubgroup group;
        private int count;
        private HashMap<Integer, HomomorphicCiphertext> ciphertexts;

        protected ReceiverSpy(LoadCurve loadCurve) {
            this.count = 0;
            ciphertexts = new HashMap<>();
            curve = loadCurve.getCurve();
            group = loadCurve.getGroup();
            field = loadCurve.getField();
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