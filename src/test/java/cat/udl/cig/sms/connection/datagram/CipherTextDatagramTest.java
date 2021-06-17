package cat.udl.cig.sms.connection.datagram;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.serializer.CipherTextDatagramSerializer;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CipherTextDatagramTest {


    private static CurveConfiguration curveConfiguration;
    private static GeneralECPoint gen;
    private static GeneralECPoint inf;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        gen = curveConfiguration.getGroup().getGenerator();
    }

    @Test
    void toByteArray() {
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(new GeneralECPoint[]{gen, gen});
        CipherTextDatagram data = new CipherTextDatagram(ciphertext);
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(data, new CipherTextDatagramSerializer(curveConfiguration).fromBytes(bytes));
    }
}