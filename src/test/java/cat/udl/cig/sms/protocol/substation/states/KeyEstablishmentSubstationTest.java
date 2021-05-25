package cat.udl.cig.sms.protocol.substation.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomControllerInt;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.data.LoadCurve;
import cat.udl.cig.sms.protocol.State;
import cat.udl.cig.sms.protocol.substation.factories.FactorySubstationState;
import cat.udl.cig.sms.protocol.substation.states.ConsumptionTransmissionSubstation;
import cat.udl.cig.sms.protocol.substation.states.KeyEstablishmentSubstation;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyEstablishmentSubstationTest {

    private FactorySubstationState factory;
    private KeyEstablishmentSubstation state;
    private final BigInteger SUM = BigInteger.TEN;
    private BigInteger EXPECTED_PRIVATE_KEY;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        SubstationBusomControllerInt controller = Mockito.mock(SubstationBusomControllerInt.class);
        Mockito.when(controller.receiveSecretKey()).then((Answer<BigInteger>) invoc -> SUM);
        ConnectionSubstation connection = Mockito.mock(ConnectionSubstation.class);
        factory = new FactorySubstationState(LoadCurve.P192(), connection);
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