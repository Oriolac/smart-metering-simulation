package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.CertificateValidation;
import udl.cig.sms.connection.SMSDatagram;

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
}
