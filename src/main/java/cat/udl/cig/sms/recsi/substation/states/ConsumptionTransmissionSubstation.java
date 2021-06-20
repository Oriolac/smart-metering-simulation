package cat.udl.cig.sms.recsi.substation.states;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.DecipherImpl;
import cat.udl.cig.sms.crypt.Decipher;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.substation.SubstationContext;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Consumption Transmission State of the Substation
 */
public class ConsumptionTransmissionSubstation implements State {

    private Decipher decipher;
    private final SubstationContext substationContext;
    private final BigInteger privateKey;
    private final int NUM_BITS = 13;

    /**
     * @param substationContext Factory that has the information of the ECC and connection and
     *                               creates the different states.
     * @param privateKey             or s0 which is the private key of the smart metering protocol
     */
    public ConsumptionTransmissionSubstation(SubstationContext substationContext, BigInteger privateKey) {
        this.substationContext = substationContext;
        decipher = new DecipherImpl(substationContext.getLoadCurve(), privateKey);
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
        substationContext.getConnection().send(new BigIntegerDatagram(t));
        List<SMSDatagram> datas = substationContext.getConnection().receive();
        if (datas.stream().allMatch(data -> data instanceof GroupElementDatagram)) {
            List<GeneralECPoint> cyphered = new ArrayList<>();
            for (SMSDatagram elem : datas) {
                GroupElement element = ((GroupElementDatagram) elem).getElement();
                cyphered.add((GeneralECPoint) element);
            }
            Optional<BigInteger> message = decipher.decrypt(cyphered, t);
            if (message.isEmpty())
                return substationContext.makeKeyEstablishment();
            return substationContext.makeConsumptionTransmission(privateKey, message.get());
        } else {
            return  substationContext.makeKeyEstablishment();
        }
    }

    /**
     * @return the private key s0
     */
    public BigInteger getPrivateKey() {
        return this.privateKey;
    }

    /**
     * @param decipher Decypher of the private key
     */
    public void setDecypher(Decipher decipher) {
        this.decipher = decipher;
    }
}
