package udl.cig.sms.connection.datagram;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.factory.FactoryCipherTextDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CipherTextDatagramTest {


    private static LoadCurve loadCurve;
    private static GeneralECPoint gen;
    private static GeneralECPoint inf;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        gen = loadCurve.getGroup().getGenerator();
    }

    @Test
    void toByteArray() {
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(new GeneralECPoint[]{gen, gen});
        CipherTextDatagram data = new CipherTextDatagram(ciphertext);
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(data, new FactoryCipherTextDatagram(loadCurve).buildDatagram(bytes));
    }
}