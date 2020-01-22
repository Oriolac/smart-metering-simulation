package udl.cig.sms.connection;

public interface Receiver {

    SMSDatagram receive(byte[] data);
}
