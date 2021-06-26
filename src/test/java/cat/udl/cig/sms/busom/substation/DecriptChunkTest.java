package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.operations.wrapper.BruteForce;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DecriptChunkTest {

    DecriptChunk currentState;
    CurveConfiguration curveConfiguration;
    ConnectionSpy connectionSpy;
    SubstationBusomContextInt substationContextInt;

    @BeforeEach
    void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        connectionSpy = new ConnectionSpy(curveConfiguration);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(connectionSpy.getPoints("6209844826947604790038975056267208146258025268519512417642",
                "2994524308329009013576298176683420492475513562601889388197", "3322243116407084751555570864568066673271311194669141092600",
                "3196076688455990248772493899225500830789111078873298446819"));
        substationContextInt = Mockito.mock(SubstationBusomContextInt.class);
        Mockito.when(substationContextInt.getConnection()).then( c -> connectionSpy);
        Mockito.when(substationContextInt.getDiscreteLogarithmAlgorithm()).then(c -> new BruteForce(curveConfiguration.getGroup().getGenerator()));
        Mockito.when(substationContextInt.makeReceiveChunk(Mockito.any())).then(c -> new ReceiveChunk(c.getArgument(0), substationContextInt));
        currentState = new DecriptChunk(curveConfiguration.getGroup(), ciphertext, substationContextInt);
    }

    @Test
    void next() throws IOException {
        BusomState nextState = currentState.next();
        assertNotNull(nextState);
    }

    @Test
    void receiveAndCompute() throws IOException {
        currentState.receiveAndCompute();
        assertEquals(1, connectionSpy.getCount());
        assertEquals(curveConfiguration.getGroup().getGenerator().pow(BigInteger.valueOf(6L)),currentState.getPartialDecryption())
        ;
    }

    @Test
    void readMessage() {
        GeneralECPoint generator = curveConfiguration.getGroup().getGenerator();
        BigInteger message = BigInteger.valueOf(6L);
        GeneralECPoint partialDecryption = generator.pow(message);
        currentState.setPartialDecryption(partialDecryption);
        assertEquals(Optional.of(message), currentState.readMessage());
        assertEquals(Optional.of(message), currentState.readMessage());
    }

    private static class ConnectionSpy implements ConnectionSubstationInt {

        private final GeneralEC curve;
        private final PrimeField field;
        private int count;
        private HashMap<Integer, GroupElement> elements;
        private ECPrimeOrderSubgroup group;

        protected ConnectionSpy(CurveConfiguration curveConfiguration) {
            this.count = 0;
            curve = curveConfiguration.getCurve();
            group = curveConfiguration.getGroup();
            field = curveConfiguration.getField();
            elements = new HashMap<>();
            addGroupElements();
        }

        private void addGroupElements() {
            elements.put(1, getPoint("3148590072366074440267992274465278396969080466506390526126", "2992299268111775915629880609173700837319807306966723823309"));
            elements.put(2, getPoint("2786264885989809290211939713098763239216982189538103461979", "1113927657884134008136506126434886123320739222265923346278"));
            elements.put(3, getPoint("638863382029225019909248102674317625151243437164237082625", "14871352334506636263566738977563240316720173953146641323"));
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
            return elements.values().stream().map(GroupElementDatagram::new).collect(Collectors.toList());
        }

        public int getCount() {
            return count;
        }

        @Override
        public int getNumberOfMeters() {
            throw new IllegalStateException();
        }

        @Override
        public void send(SMSDatagram data) throws IOException {
            throw new IllegalStateException();
        }
    }
}