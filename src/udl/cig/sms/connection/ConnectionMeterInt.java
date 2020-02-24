package udl.cig.sms.connection;

import java.io.IOException;

/**
 * Interface that extends ReceiverMeter and Sender
 */
public interface ConnectionMeterInt extends ReceiverMeter, Sender {
    void close() throws IOException;
}
