package cat.udl.cig.sms.connection.datagram;

import cat.udl.cig.sms.connection.Datagrams;

public class KeyRenewalDatagram implements SMSDatagram {
    /**
     * @return byte[] that it's the content of the datagram with the type
     */
    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) Datagrams.KEY_RENEWAL_DATAGRAM.ordinal();
        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KeyRenewalDatagram;
    }
}
