package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.connection.SMSDatagram;

public class NeighborhoodDatagram implements SMSDatagram {

    private final String certificate;
    private final GroupElement publicKey;

    public NeighborhoodDatagram(GroupElement publicKey, String certificate) {
        this.publicKey = publicKey;
        this.certificate = certificate;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    public String getCertificate() {
        return certificate;
    }

    public GroupElement getPublicKey() {
        return publicKey;
    }
}
