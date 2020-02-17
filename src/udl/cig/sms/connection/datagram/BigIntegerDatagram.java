package udl.cig.sms.connection.datagram;

import udl.cig.sms.connection.Datagrams;

import java.math.BigInteger;
import java.util.Objects;

/**
 * SMSDatagram that contains a BigInteger
 */
public class BigIntegerDatagram implements SMSDatagram {

    private final BigInteger temporal;

    public BigIntegerDatagram(BigInteger temporal) {
        this.temporal = temporal;
    }

    /**
     * @return byte[] that it's the content of the datagram with the type
     */
    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) Datagrams.BIG_INTEGER_DATAGRAM.ordinal();
        byte[] temp = this.temporal.toByteArray();
        System.arraycopy(temp, 0, bytes, bytes.length - temp.length, temp.length);
        return bytes;
    }

    /**
     * @return the BigInteger
     */
    public BigInteger getTemporal() {
        return temporal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigIntegerDatagram that = (BigIntegerDatagram) o;
        return Objects.equals(temporal, that.temporal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temporal);
    }

    @Override
    public String toString() {
        return "BigIntegerDatagram{" +
                "temporal=" + temporal +
                '}';
    }
}
