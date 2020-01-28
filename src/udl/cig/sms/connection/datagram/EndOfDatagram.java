package udl.cig.sms.connection.datagram;

import udl.cig.sms.connection.Datagrams;

public class EndOfDatagram implements SMSDatagram {

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) Datagrams.END_OF_DATAGRAM.ordinal();
        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EndOfDatagram;
    }
}
