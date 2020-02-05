package udl.cig.sms.protocol.meter.states;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.datagram.BigIntegerDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.crypt.Cypher;
import udl.cig.sms.crypt.CypherMessage;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;

import java.io.IOException;

public class ConsumptionTransmission implements State {

    private FactoryMeterState factory;
    private final PrimeFieldElement privateKey;
    private final Cypher cypher;

    public ConsumptionTransmission(FactoryMeterState factory,
                                   PrimeFieldElement privateKey) {
        this.factory = factory;
        this.privateKey = privateKey;
        cypher = new CypherMessage(factory.getLoadCurve(), this.privateKey.getIntValue());
    }

    @Override
    public State next() throws IOException, NullMessageException {
        System.out.println("Getting t");
        SMSDatagram data = factory.getConnection().receive();
        System.out.println("Received t");
        if (data instanceof BigIntegerDatagram) {
            GeneralECPoint cypherMessage;
            cypherMessage = cypher.encrypt(factory.getConsumption().read(),
                    ((BigIntegerDatagram) data).getTemporal());
            factory.getConnection().send(new GroupElementDatagram(cypherMessage));
            return this;
        }
        return factory.makeKeyEstablishment();
    }
}

