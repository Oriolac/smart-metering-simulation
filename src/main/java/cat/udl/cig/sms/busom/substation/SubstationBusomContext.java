package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.operations.wrapper.BruteForce;
import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.certificate.CertificateTrueMock;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class SubstationBusomContext implements SubstationBusomContextInt {

    private final ConnectionSubstationInt connection;
    private final CertificateTrueMock<String> certificateValidation;
    private BusomState state;
    private final LogarithmAlgorithm logarithmAlgorithm;

    public SubstationBusomContext(MultiplicativeSubgroup group, ConnectionSubstationInt connection) {
        this.connection = connection;
        logarithmAlgorithm = new BruteForce(group.getGenerator());
        this.certificateValidation = new CertificateTrueMock<String>();
        this.state = new BusomSubstationSetUp(group, this);
    }

    @Override
    public void setUp() throws IOException, NullMessageException {
        if (!(state instanceof BusomSubstationSetUp)) {
            throw new IllegalStateException();
        }
        this.state = state.next();
    }

    @Override
    public void computeC() throws IOException, NullMessageException {
        if (!(state instanceof ReceiveChunk)) {
            throw new IllegalStateException();
        }
        this.state = state.next();
    }

    @Override
    public Optional<BigInteger> decrypt() throws IOException, NullMessageException {
        if (!(state instanceof  DecriptChunk)) {
            throw new IllegalStateException();
        }
        BusomState receiveChunk = state.next();
        Optional<BigInteger> message = ((DecriptChunk) state).readMessage();
        this.state = receiveChunk;
        return message;
    }

    @Override
    public ConnectionSubstationInt getConnection() {
        return this.connection;
    }

    @Override
    public LogarithmAlgorithm getDiscreteLogarithmAlgorithm() {
        return this.logarithmAlgorithm;
    }

    @Override
    public ReceiveChunk makeReceiveChunk(MultiplicativeSubgroup group) {
        return new ReceiveChunk(group, this);
    }

    @Override
    public DecriptChunk makeDecriptChunk(MultiplicativeSubgroup group, HomomorphicCiphertext ciphertext) {
        return new DecriptChunk(group, ciphertext, this);
    }

    public CertificateTrueMock<String> getCertificateValidation() {
        return certificateValidation;
    }
}