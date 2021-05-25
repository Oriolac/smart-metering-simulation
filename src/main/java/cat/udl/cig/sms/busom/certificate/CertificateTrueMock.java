package cat.udl.cig.sms.busom.certificate;

import cat.udl.cig.fields.GroupElement;

/**
 * As certificate was out of the scope of the initial project,
 * it returns always true.
 *
 * @param <T> Type of the certificate
 */
public class CertificateTrueMock<T> implements CertificateValidation<T> {

    /**
     * @param element     to certificate.
     * @param certificate lets the substation calculate if the element is correct
     * @return true always
     */
    @Override
    public boolean validateCertificate(GroupElement element, T certificate) {
        return true;
    }
}
