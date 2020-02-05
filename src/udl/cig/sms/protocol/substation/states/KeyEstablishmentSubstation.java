package udl.cig.sms.protocol.substation.states;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.busom.SubstationBusomControllerInt;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.IOException;
import java.math.BigInteger;

public class KeyEstablishmentSubstation implements State {


    private SubstationBusomControllerInt controller;
    private FactorySubstationState factory;

    public KeyEstablishmentSubstation(FactorySubstationState factory) {
        this.factory = factory;
        this.controller = factory.makeSubstationBusomController();
    }

    public void setController(SubstationBusomControllerInt controller) {
        this.controller = controller;
    }

    @Override
    public State next() throws IOException, NullMessageException {
        BigInteger privateKey = controller.receiveSecretKey();
        BigInteger order = factory.getLoadCurve().getGroup().getSize();
        privateKey = privateKey.negate().add(order).remainder(order);
        return factory.makeConsumptionTransmission(privateKey);
    }
}
