package cat.udl.cig.sms.recsi.substation.states;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomServiceInt;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.substation.SubstationStateContext;

import java.io.IOException;
import java.math.BigInteger;

/**
 * The Key Establishment State of the Substation
 */
public class KeyEstablishmentSubstation implements State {


    private SubstationBusomServiceInt service;
    private final SubstationStateContext context;

    /**
     * @param context Factory that has the information of the ECC and connection and
     *                creates the different states.
     */
    public KeyEstablishmentSubstation(SubstationStateContext context) {
        this.context = context;
        this.service = context.makeSubstationBusomController();
    }

    /**
     * @param service of the busom
     */
    public void setService(SubstationBusomServiceInt service) {
        this.service = service;
    }

    /**
     * @return the next state
     * @throws IOException          in case it fails the IO methods
     * @throws NullMessageException in case the message is null
     */
    @Override
    public State next() throws IOException, NullMessageException {
        BigInteger order = context.getLoadCurve().getGroup().getSize();
        try {
            BigInteger privateKey = service.receiveSecretKey().remainder(order);
            privateKey = order.subtract(privateKey);
            return context.makeConsumptionTransmission(privateKey);
        } catch (NullMessageException exception) {
            return context.makeKeyEstablishment();
        }
    }
}
