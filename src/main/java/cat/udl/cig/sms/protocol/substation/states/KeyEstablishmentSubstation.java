package cat.udl.cig.sms.protocol.substation.states;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomControllerInt;
import cat.udl.cig.sms.protocol.State;
import cat.udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.IOException;
import java.math.BigInteger;

/**
 * The Key Establishment State of the Substation
 */
public class KeyEstablishmentSubstation implements State {


    private SubstationBusomControllerInt controller;
    private FactorySubstationState factory;

    /**
     * @param factory Factory that has the information of the ECC and connection and
     *                creates the different states.
     */
    public KeyEstablishmentSubstation(FactorySubstationState factory) {
        this.factory = factory;
        this.controller = factory.makeSubstationBusomController();
    }

    /**
     * @param controller of the busom
     */
    public void setController(SubstationBusomControllerInt controller) {
        this.controller = controller;
    }

    /**
     * @return the next state
     * @throws IOException          in case it fails the IO methods
     * @throws NullMessageException in case the message is null
     */
    @Override
    public State next() throws IOException, NullMessageException {
        BigInteger order = factory.getLoadCurve().getGroup().getSize();
        BigInteger privateKey = controller.receiveSecretKey().remainder(order);
        privateKey = order.subtract(privateKey);
        return factory.makeConsumptionTransmission(privateKey);
    }
}
