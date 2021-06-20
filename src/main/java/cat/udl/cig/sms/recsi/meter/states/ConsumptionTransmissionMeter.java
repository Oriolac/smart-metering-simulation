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

/**
 * Meter state that represents the consumption transmission.
 */
public class ConsumptionTransmissionMeter implements State {

    private final MeterStateContext factory;
    private final Cypher cypher;

    /**
     * @param factory    Factory that has the information of the ECC and connection and
     *                   creates the different states.
     * @param privateKey or s0 which is the private key of the smart metering protocol
     */
    public ConsumptionTransmissionMeter(MeterStateContext factory,
                                        PrimeFieldElement privateKey) {
        this.factory = factory;
        cypher = new CypherImpl(factory.getLoadCurve(), privateKey.getIntValue());
    }

    /**
     * @return the next state
     * @throws IOException          in case that IO fails
     * @throws NullMessageException in case the message is null
     */
    @Override
    public State next() throws IOException, NullMessageException {
        SMSDatagram data = factory.getConnection().receive();
        if (data instanceof BigIntegerDatagram) {
            GeneralECPoint cypherMessage;
            cypherMessage = cypher.encrypt(factory.getConsumption().read(),
                    ((BigIntegerDatagram) data).getBigInteger());
            factory.getConnection().send(new GroupElementDatagram(cypherMessage));
            return this;
        }
        return factory.makeKeyEstablishment();
    }
}

