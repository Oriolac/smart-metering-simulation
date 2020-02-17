package udl.cig.sms.connection.datagram;

/**
 * Represents the datagram of the Smart Metering Simulation
 */
public interface SMSDatagram {

    /**
     * @return byte[] that it's the content of the datagram with the type
     */
    byte[] toByteArray();
}
