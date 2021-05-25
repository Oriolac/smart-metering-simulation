package sms.connection.factory;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.factory.FactoryCipherTextDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryCipherTextDatagramTest {


    private static LoadCurve loadCurve;
    private static FactoryCipherTextDatagram factory;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        factory = new FactoryCipherTextDatagram(loadCurve);
    }

    @Test
    void buildDatagram() {
        byte[] bytes = new byte[factory.getByteSize()];
        byte[] bGenerator = generatorToBytes();
        GroupElement gen = loadCurve.getGroup().getGenerator();
        System.arraycopy(bGenerator, 0, bytes, 0, bGenerator.length);
        System.arraycopy(bGenerator, 0, bytes, bGenerator.length, bGenerator.length);
        assertEquals(new CipherTextDatagram(new ElGamalCiphertext(new GroupElement[]{gen, gen})),
                factory.buildDatagram(bytes));
    }

    static byte[] generatorToBytes() {
        byte[] bytes = new byte[factory.getByteSize()/2];
        byte[] x = loadCurve.getGroup().getGenerator().getIntValue().toByteArray();
        byte[] y = loadCurve.getGroup().getGenerator().getY().getIntValue().toByteArray();
        System.arraycopy(x, 0, bytes, bytes.length / 2 - x.length, x.length);
        System.arraycopy(y, 0, bytes, bytes.length - y.length, y.length);
        return bytes;
    }

    @Test
    void getByteSize() {
        assertEquals((192 + 8) * 4 / 8, factory.getByteSize());
    }
}