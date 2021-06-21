package cat.udl.cig.sms.recsi.meter.states;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.Cypher;
import cat.udl.cig.sms.crypt.CypherImpl;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Meter state that represents the consumption transmission.
 */
public class ConsumptionTransmissionMeter implements State {

    private final MeterStateContext context;
    private final Cypher cypher;

    /**
     * @param context    Factory that has the information of the ECC and connection and
     *                   creates the different states.
     * @param privateKey or s0 which is the private key of the smart metering protocol
     */
    public ConsumptionTransmissionMeter(MeterStateContext context,
                                        PrimeFieldElement privateKey) {
        this.context = context;
        cypher = new CypherImpl(context.getLoadCurve(), privateKey.getIntValue());
    }

    /**
     * @return the next state
     * @throws IOException          in case that IO fails
     * @throws NullMessageException in case the message is null
     */
    @Override
    public State next() throws IOException, NullMessageException {
        SMSDatagram data = context.getConnection().receive();
        if (data instanceof BigIntegerDatagram) {
            GeneralECPoint cypherMessage;
            BigInteger consumption = context.getConsumption().read();
            cypherMessage = cypher.encrypt(consumption,
                    ((BigIntegerDatagram) data).getBigInteger());
            context.getConnection().send(new GroupElementDatagram(cypherMessage));
            return this;
        }
        return context.makeKeyEstablishment();
    }
}

