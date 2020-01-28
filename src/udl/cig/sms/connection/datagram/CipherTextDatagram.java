package udl.cig.sms.connection.datagram;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;

public class CipherTextDatagram implements SMSDatagram {

    private final HomomorphicCiphertext ciphertext;

    public CipherTextDatagram(HomomorphicCiphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

    public HomomorphicCiphertext getCiphertext() {
        return ciphertext;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
