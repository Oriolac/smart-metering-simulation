package cat.udl.cig.sms.recsi.substation.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.Decipher;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.SubstationContextSubstation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumptionTransmissionMeterSubstationTest {


    private SubstationContextSubstation factory;
    private ConsumptionTransmissionSubstation state;
    private final BigInteger SUM = BigInteger.TWO;
    private final BigInteger PRIVATE_KEY = CurveConfiguration.P192().getField().toElement(SUM).opposite().getIntValue();
    private final BigInteger MESSAGE = BigInteger.valueOf(10);
    private ConnectionSubstation connection;

    @BeforeEach
    void setUp() {
        Decipher decipher = Mockito.mock(Decipher.class);
        Mockito.when(decipher.decrypt(Mockito.any(), Mockito.any(BigInteger.class)))
                .then((Answer<Optional<BigInteger>>) invoc -> Optional.of(MESSAGE));
        connection = Mockito.mock(ConnectionSubstation.class);
        factory = new SubstationContextSubstation(CurveConfiguration.P192(), connection);
        state = factory.makeConsumptionTransmission(PRIVATE_KEY);
        state.setDecypher(decipher);
    }

    @Test
    void next() throws IOException, NullMessageException {
        Mockito.when(connection.receive())
                .then((Answer<List<SMSDatagram>>) invocationOnMock ->
                        Collections.singletonList(new GroupElementDatagram(factory.getLoadCurve().getCurve().getRandomElement())));
        assertTrue(state.next() instanceof ConsumptionTransmissionSubstation);
        Mockito.verify(connection, Mockito.times(1)).send(Mockito.any(BigIntegerDatagram.class));
        Mockito.verify(connection, Mockito.times(1)).receive();
    }

    //TODO : expected KeyEstablishment.
}