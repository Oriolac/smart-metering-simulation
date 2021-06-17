package cat.udl.cig.sms.connection.datagram;

import cat.udl.cig.sms.connection.Datagrams;

import java.math.BigInteger;
import java.util.Objects;

/**
 * SMSDatagram that contains a BigInteger
 */
public class BigIntegerDatagram implements SMSDatagram {

    private final BigInteger bigInteger;

    public BigIntegerDatagram(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    /**
     * @return byte[] that it's the content of the datagram with the type
     */
    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) Datagrams.BIG_INTEGER_DATAGRAM.ordinal();
        byte[] temp = this.bigInteger.toByteArray();
        System.arraycopy(temp, 0, bytes, bytes.length - temp.length, temp.length);
        return bytes;
    }

    /**
     * @return the BigInteger
     */
    public BigInteger getBigInteger() {
        return bigInteger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigIntegerDatagram that = (BigIntegerDatagram) o;
        return Objects.equals(bigInteger, that.bigInteger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bigInteger);
    }

    @Override
    public String toString() {
        return "BigIntegerDatagram{" +
                "temporal=" + bigInteger +
                '}';
    }
}
