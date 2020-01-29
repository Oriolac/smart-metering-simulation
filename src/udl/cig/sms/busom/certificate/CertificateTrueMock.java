package udl.cig.sms.busom.certificate;

import cat.udl.cig.fields.GroupElement;

public class CertificateTrueMock<T> implements CertificateValidation<T> {
    @Override
    public boolean validateCertificate(GroupElement element, T certificate) {
        return true;
    }
}
