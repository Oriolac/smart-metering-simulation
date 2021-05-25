package cat.udl.cig.sms.busom.certificate;

import cat.udl.cig.fields.GroupElement;

/**
 * Certificate validation: Given a group Element and a certificate, it
 * validates the value.
 *
 * @param <T> type of the certificate
 */
public interface CertificateValidation<T> {

    /**
     * @param element     to certificate.
     * @param certificate lets the substation calculate if the element is correct
     * @return true if the certificate valueates true the element, false otherwise
     */
    boolean validateCertificate(GroupElement element, T certificate);
}
