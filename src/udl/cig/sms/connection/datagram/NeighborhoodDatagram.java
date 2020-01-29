package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.certificate.CertificateValidation;

import java.util.Objects;

public class NeighborhoodDatagram<T> implements SMSDatagram {

    private final T certificate;
    private final GroupElement publicKey;

    public NeighborhoodDatagram(GroupElement publicKey, T certificate) {
        this.publicKey = publicKey;
        this.certificate = certificate;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
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
