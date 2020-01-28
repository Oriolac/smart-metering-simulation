package udl.cig.sms.connection.datagram;

public class EndOfDatagram implements SMSDatagram {
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EndOfDatagram;
    }
}
