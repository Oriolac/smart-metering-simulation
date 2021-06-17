package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryCipherTextDatagramTest {


    private static CurveConfiguration curveConfiguration;
    private static CipherTextDatagramSerializer factory;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        factory = new CipherTextDatagramSerializer(curveConfiguration);
    }

    @Test
    void buildDatagram() {
        byte[] bytes = new byte[factory.getByteSize()];
        byte[] bGenerator = generatorToBytes();
        GroupElement gen = curveConfiguration.getGroup().getGenerator();
        System.arraycopy(bGenerator, 0, bytes, 0, bGenerator.length);
        System.arraycopy(bGenerator, 0, bytes, bGenerator.length, bGenerator.length);
        assertEquals(new CipherTextDatagram(new ElGamalCiphertext(new GroupElement[]{gen, gen})),
                factory.fromBytes(bytes));
    }

    static byte[] generatorToBytes() {
        byte[] bytes = new byte[factory.getByteSize()/2];
        byte[] x = curveConfiguration.getGroup().getGenerator().getIntValue().toByteArray();
        byte[] y = curveConfiguration.getGroup().getGenerator().getY().getIntValue().toByteArray();
        System.arraycopy(x, 0, bytes, bytes.length / 2 - x.length, x.length);
        System.arraycopy(y, 0, bytes, bytes.length - y.length, y.length);
        return bytes;
    }

    @Test
    void getByteSize() {
        assertEquals((192 + 8) * 4 / 8, factory.getByteSize());
    }
}