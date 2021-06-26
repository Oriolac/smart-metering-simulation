package cat.udl.cig.sms.recsi.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumptionTransmissionMeterTest {


    private ConnectionMeterInt connectionMeter;
    private ConsumptionReader consumptionReader;
    private MeterStateContext context;
    private PrimeFieldElement privateKey;
    private ConsumptionTransmissionMeter consumptTrans;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        connectionMeter = Mockito.mock(ConnectionMeterInt.class);
        consumptionReader = Mockito.mock(ConsumptionReader.class);
        Mockito.when(consumptionReader.read()).then((Answer<BigInteger>) invoc -> BigInteger.ONE);
        privateKey = CurveConfiguration.P192().getField().toElement(BigInteger.ONE);
    }

    @Test
    void correctNext() throws IOException, NullMessageException {
        Mockito.when(connectionMeter.receive()).then((Answer<BigIntegerDatagram>) invocationOnMock -> new BigIntegerDatagram(BigInteger.ONE));
        context = new MeterStateContext(1, CurveConfiguration.P192(), connectionMeter, consumptionReader,"");
        consumptTrans = new ConsumptionTransmissionMeter(context, privateKey);
        assertTrue(consumptTrans.next() instanceof ConsumptionTransmissionMeter);
        Mockito.verify(connectionMeter, Mockito.times(2))
                .send(Mockito.any(SMSDatagram.class));
    }

    @Test
    void badNext() throws IOException, NullMessageException {
        Mockito.when(connectionMeter.receive()).then((Answer<SMSDatagram>) invocationOnMock -> new EndOfDatagram());
        context = new MeterStateContext(1, CurveConfiguration.P192(), connectionMeter, consumptionReader,"");
        consumptTrans = new ConsumptionTransmissionMeter(context, privateKey);
        assertTrue(consumptTrans.next() instanceof KeyEstablishmentMeter);
        Mockito.verify(connectionMeter, Mockito.times(2))
                .send(Mockito.any(SMSDatagram.class));
    }
}