package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.certificate.CertificateValidation;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public interface SubstationBusomContextInt {

    void setUp() throws IOException, NullMessageException;

    void computeC() throws IOException, NullMessageException;

    Optional<BigInteger> decrypt() throws IOException, NullMessageException;

    ConnectionSubstationInt getConnection();

    LogarithmAlgorithm getDiscreteLogarithmAlgorithm();

    ReceiveChunk makeReceiveChunk(MultiplicativeSubgroup group);

    DecriptChunk makeDecriptChunk(MultiplicativeSubgroup group, HomomorphicCiphertext ciphertext);

    CertificateValidation<String> getCertificateValidation();
}
