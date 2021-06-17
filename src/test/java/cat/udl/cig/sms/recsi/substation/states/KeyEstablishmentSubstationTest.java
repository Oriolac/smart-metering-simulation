package cat.udl.cig.sms.recsi.substation.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomControllerInt;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.substation.SubstationContext;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyEstablishmentSubstationTest {

    private SubstationContext factory;
    private KeyEstablishmentSubstation state;
    private final BigInteger SUM = BigInteger.TEN;
    private BigInteger EXPECTED_PRIVATE_KEY;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        SubstationBusomControllerInt controller = Mockito.mock(SubstationBusomControllerInt.class);
        Mockito.when(controller.receiveSecretKey()).then((Answer<BigInteger>) invoc -> SUM);
        ConnectionSubstation connection = Mockito.mock(ConnectionSubstation.class);
        factory = new SubstationContext(CurveConfiguration.P192(), connection);
        BigInteger order = factory.getLoadCurve().getGroup().getSize();
        EXPECTED_PRIVATE_KEY = SUM.negate().add(order).remainder(order);
        state = factory.makeKeyEstablishment();
        state.setController(controller);
    }

    @Test
    void next() throws IOException, NullMessageException {
        State nextState = state.next();
        assertTrue(nextState instanceof ConsumptionTransmissionSubstation);
        ConsumptionTransmissionSubstation currentState = (ConsumptionTransmissionSubstation) nextState;
        assertEquals(EXPECTED_PRIVATE_KEY, currentState.getPrivateKey());
    }
}