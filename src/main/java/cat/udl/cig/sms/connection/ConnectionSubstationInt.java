package cat.udl.cig.sms.connection;

/**
 * Interface that extends the receiver's Substation and the sender.
 */
public interface ConnectionSubstationInt extends ReceiverSubstation, Sender {

    int getNumberOfMeters();
}
