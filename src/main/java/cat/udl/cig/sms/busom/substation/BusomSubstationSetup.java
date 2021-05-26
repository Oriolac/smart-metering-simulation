package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.certificate.CertificateTrueMock;
import cat.udl.cig.sms.busom.certificate.CertificateValidation;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.connection.ReceiverSubstation;
import cat.udl.cig.sms.connection.Sender;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Busom Set up for substations.
 */
public class BusomSubstationSetup implements BusomState {

    private final MultiplicativeSubgroup group;
    private ConnectionSubstationInt connection;
    private ReceiverSubstation receiver;
    private Sender sender;
    private List<NeighborhoodDatagram<String>> datagrams;
    private CertificateValidation<String> validation;
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    /**
     * Sets up the parameters needed for the ecryption. Used for testing
     *
     * @param group multiplicative group.
     */
    protected BusomSubstationSetup(MultiplicativeSubgroup group) {
        this.group = group;
        datagrams = new ArrayList<>();
    }

    /**
     * Generates a BusomSubstationSetup
     *
     * @param group      multiplicative group.
     * @param connection to all meters
     */
    public BusomSubstationSetup(MultiplicativeSubgroup group, ConnectionSubstationInt connection) {
        this.group = group;
        datagrams = new ArrayList<>();
        this.connection = connection;
        this.sender = connection;
        this.receiver = connection;
        this.validation = new CertificateTrueMock<>();
    }

    /**
     * Next state of the protocol
     *
     * @return next state of the protocol (ReceiveChunk)
     * @throws IOException if connection fails.
     */
    @Override
    public BusomState next() throws IOException {
        receivePublicKeys();
        sendPublicKey();
        return new ReceiveChunk(group, connection);
    }

    /**
     * Receives public keys of all meters.
     *
     * @throws IOException if Connection fails.
     */
    @SuppressWarnings("unchecked cast")
    protected void receivePublicKeys() throws IOException {
        for (SMSDatagram data : receiver.receive()) {
            if (data instanceof NeighborhoodDatagram) {
                receivePublicMeterKey((NeighborhoodDatagram<String>) data);
            } else {
                LOGGER.log(Level.WARNING, "Not permitted data type.");
            }
        }
    }

    /**
     * Updates list of datagrams if data is validated.
     *
     * @param data a NeighborHood datagram
     */
    private void receivePublicMeterKey(NeighborhoodDatagram<String> data) {
        if (data.validate(validation))
            datagrams.add(data);
    }

    /**
     * Sends all the collected datagrams to the meters
     *
     * @throws IOException if connection fails.
     */
    protected void sendPublicKey() throws IOException {
        for (NeighborhoodDatagram<String> data : datagrams) {
            sender.send(data);
        }
        sender.send(new EndOfDatagram());
    }

    /**
     * Sets the validation. Used for tests.
     *
     * @param validation mock of validation.
     */
    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }

    /**
     * Sets the receiver of the substation. Used for tests.
     *
     * @param receiver mock of sender
     */
    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }

    /**
     * Sets the sender of the substation. Used for tests.
     *
     * @param sender mock of sender.
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * Sets the list of validated datagrams. Used for testing
     *
     * @param datagrams list of NHDatagrams.
     */
    public void setDatagrams(List<NeighborhoodDatagram<String>> datagrams) {
        this.datagrams = datagrams;
    }
}
