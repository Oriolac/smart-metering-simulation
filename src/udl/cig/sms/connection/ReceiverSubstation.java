package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.List;

public interface ReceiverSubstation {

    List<SMSDatagram> receive() throws IOException;
}
