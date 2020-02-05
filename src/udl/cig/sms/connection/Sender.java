package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;

public interface Sender {

    void send(SMSDatagram data) throws IOException;
}