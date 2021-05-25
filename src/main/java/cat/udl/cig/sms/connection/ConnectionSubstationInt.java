package cat.udl.cig.sms.connection;

/**
 * Interface that extends the receiver's Substation and the sender.
 */
public interface ConnectionSubstationInt extends ReceiverSubstation, Sender {

    /**
     * @return the number of meters that supports the server, our susbstation.
     */
    int getNumberOfMeters();
}
