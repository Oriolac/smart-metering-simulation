package cat.udl.cig.sms.connection.datagram;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.sms.connection.Datagrams;

import java.util.Objects;

/**
 * SMSDatagram that contains the CipherText
 */
public class CipherTextDatagram implements SMSDatagram {

    private final HomomorphicCiphertext ciphertext;

    public CipherTextDatagram(HomomorphicCiphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

    public HomomorphicCiphertext getCiphertext() {
        return ciphertext;
    }

    /**
     * @return byte[] that it's the content of the datagram with the type
     */
    @Override
    public byte[] toByteArray() {
        byte[] c = ciphertext.getParts()[0].toBytes();
        byte[] d = ciphertext.getParts()[1].toBytes();
        byte[] res = new byte[c.length + d.length + 1];
        res[0] = (byte) Datagrams.CIPHER_TEXT_DATAGRAM.ordinal();
        System.arraycopy(c, 0, res, 1, c.length);
        System.arraycopy(d, 0, res, c.length + 1, d.length);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CipherTextDatagram that = (CipherTextDatagram) o;
        return Objects.equals(ciphertext, that.ciphertext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ciphertext);
    }

    @Override
    public String toString() {
        return "CipherTextDatagram{" +
                "ciphertext=" + ciphertext +
                '}';
    }
}
