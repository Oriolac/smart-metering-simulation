package udl.cig.sms.protocol.substation.states;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.datagram.BigIntegerDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.crypt.DecipherMessage;
import udl.cig.sms.crypt.Decypher;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ConsumptionTransmissionSubstation implements State {

    private Decypher decipher;
    private FactorySubstationState factorySubstationState;
    private BigInteger privateKey;
    private final int NUM_BITS = 13;

    public ConsumptionTransmissionSubstation(FactorySubstationState factorySubstationState, BigInteger privateKey) {
        this.factorySubstationState = factorySubstationState;
        decipher = new DecipherMessage(factorySubstationState.getLoadCurve(), privateKey);
        this.privateKey = privateKey;
    }

    @Override
    public State next() throws IOException, NullMessageException {
        BigInteger t = new BigInteger(NUM_BITS, new SecureRandom());
        System.out.println("Temporal big integer.");
        factorySubstationState.getConnection().send(new BigIntegerDatagram(t));
        System.out.println("Sended t");
        List<SMSDatagram> datas = factorySubstationState.getConnection().receive();
        System.out.println("Retrieved data");
        if(datas.stream().allMatch(data -> data instanceof GroupElementDatagram)) {
            List<GeneralECPoint> cyphered = new ArrayList<>();
            for (SMSDatagram elem : datas) {
                GroupElement element = ((GroupElementDatagram) elem).getElement();
                cyphered.add((GeneralECPoint) element);
            }
            System.out.println("Printing decyphered: ");
            System.out.println(decipher.decrypt(cyphered, t));
            //TODO : This needs to be changed to send it to the station.
            return this;
        }
        return new KeyEstablishmentSubstation(factorySubstationState);
    }

    public BigInteger getPrivateKey() {
        return this.privateKey;
    }

    public void setDecypher(Decypher decypher) {
        this.decipher = decypher;
    }
}
