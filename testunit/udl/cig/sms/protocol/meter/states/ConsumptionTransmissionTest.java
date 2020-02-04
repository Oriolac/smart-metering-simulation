package udl.cig.sms.protocol.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.connection.datagram.BigIntegerDatagram;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.consumption.ConsumptionReader;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumptionTransmissionTest {


    private ConnectionMeterInt connectionMeter;
    private ConsumptionReader consumptionReader;
    private FactoryMeterState factory;
    private PrimeFieldElement privateKey;
    private ConsumptionTransmission consumptTrans;

    @BeforeEach
    void setUp() {
        connectionMeter = Mockito.mock(ConnectionMeterInt.class);
        consumptionReader = Mockito.mock(ConsumptionReader.class);
        Mockito.when(consumptionReader.read()).then((Answer<BigInteger>) invoc -> BigInteger.ONE);
        factory = new FactoryMeterState(LoadCurve.P192(), connectionMeter, consumptionReader,"");
        privateKey = LoadCurve.P192().getField().toElement(BigInteger.ONE);
        consumptTrans = new ConsumptionTransmission(factory, privateKey);
    }

    @Test
    void correctNext() throws IOException, NullMessageException {
        Mockito.when(connectionMeter.receive()).then((Answer<BigIntegerDatagram>) invocationOnMock -> new BigIntegerDatagram(BigInteger.ONE));
        assertTrue(consumptTrans.next() instanceof ConsumptionTransmission);
        Mockito.verify(connectionMeter, Mockito.times(1))
                .send(Mockito.any(SMSDatagram.class));
    }

    @Test
    void badNext() throws IOException, NullMessageException {
        Mockito.when(connectionMeter.receive()).then((Answer<SMSDatagram>) invocationOnMock -> new EndOfDatagram());
        assertTrue(consumptTrans.next() instanceof KeyEstablishment);
        Mockito.verify(connectionMeter, Mockito.times(0))
                .send(Mockito.any(SMSDatagram.class));
    }
}