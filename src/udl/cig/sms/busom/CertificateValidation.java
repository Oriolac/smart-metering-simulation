package udl.cig.sms.busom;

import cat.udl.cig.fields.GroupElement;

public interface CertificateValidation<T> {
    boolean validateCertificate(GroupElement element, T certificate);
}
