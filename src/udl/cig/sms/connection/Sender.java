package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;

public interface Sender {

    void send(SMSDatagram data);
}
