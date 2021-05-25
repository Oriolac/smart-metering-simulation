package cat.udl.cig.sms.protocol.substation.states;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.DecipherMessage;
import cat.udl.cig.sms.crypt.Decypher;
import cat.udl.cig.sms.protocol.State;
import cat.udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Consumption Transmission State of the Substation
 */
public class ConsumptionTransmissionSubstation implements State {

    private Decypher decipher;
    private FactorySubstationState factorySubstationState;
    private BigInteger privateKey;
    private final int NUM_BITS = 13;

    /**
     * @param factorySubstationState Factory that has the information of the ECC and connection and
     *                               creates the different states.
     * @param privateKey             or s0 which is the private key of the smart metering protocol
     */
    public ConsumptionTransmissionSubstation(FactorySubstationState factorySubstationState, BigInteger privateKey) {
        this.factorySubstationState = factorySubstationState;
        decipher = new DecipherMessage(factorySubstationState.getLoadCurve(), privateKey);
        this.privateKey = privateKey;
    }

    /**
     * @return the next state
     * @throws IOException          in case that the IO fails
     * @throws NullMessageException in case that the message is null
     */
    @Override
    public State next() throws IOException, NullMessageException {
        BigInteger t = new BigInteger(NUM_BITS, new SecureRandom());
        factorySubstationState.getConnection().send(new BigIntegerDatagram(t));
        List<SMSDatagram> datas = factorySubstationState.getConnection().receive();
        if (datas.stream().allMatch(data -> data instanceof GroupElementDatagram)) {
            List<GeneralECPoint> cyphered = new ArrayList<>();
            for (SMSDatagram elem : datas) {
                GroupElement element = ((GroupElementDatagram) elem).getElement();
                cyphered.add((GeneralECPoint) element);
            }
            System.out.println("Decyphered: " + decipher.decrypt(cyphered, t));
            return this;
        }
        return new KeyEstablishmentSubstation(factorySubstationState);
    }

    /**
     * @return the private key s0
     */
    public BigInteger getPrivateKey() {
        return this.privateKey;
    }

    /**
     * @param decypher Decypher of the private key
     */
    public void setDecypher(Decypher decypher) {
        this.decipher = decypher;
    }
}
