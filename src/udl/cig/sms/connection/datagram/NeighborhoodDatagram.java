package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.certificate.CertificateValidation;
import udl.cig.sms.connection.Datagrams;

import java.util.Objects;

public class NeighborhoodDatagram<T> implements SMSDatagram {

    private final T certificate;
    private final GroupElement publicKey;
    static private final int LENGTH_BYTES_CERTIFICATE = 8 * Character.BYTES;

    public NeighborhoodDatagram(GroupElement publicKey, T certificate) {
        this.publicKey = publicKey;
        this.certificate = certificate;
    }

    @Override
    public byte[] toByteArray() {
        byte[] element = publicKey.toBytes();
        byte[] res = new byte[1 + element.length + LENGTH_BYTES_CERTIFICATE];
        res[0] = (byte) Datagrams.NEIGHBORHOOD_DATAGRAM.ordinal();
        System.arraycopy(element, 0, res, 1, element.length);
        return res;
    }

    public T getCertificate() {
        return certificate;
    }

    public GroupElement getPublicKey() {
        return publicKey;
    }

    public boolean validate(CertificateValidation<T> validation) {
        return validation.validateCertificate(publicKey, certificate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeighborhoodDatagram<?> that = (NeighborhoodDatagram<?>) o;
        return Objects.equals(certificate, that.certificate) &&
                Objects.equals(publicKey, that.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate, publicKey);
    }

    @Override
    public String toString() {
        return "NeighborhoodDatagram{" +
                "certificate=" + certificate +
                ", publicKey=" + publicKey +
                '}';
    }
}
