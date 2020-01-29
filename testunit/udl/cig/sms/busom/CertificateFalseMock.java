package udl.cig.sms.busom;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.certificate.CertificateValidation;

public class CertificateFalseMock<T> implements CertificateValidation<T> {
    @Override
    public boolean validateCertificate(GroupElement element, T certificate) {
        return false;
    }
}
