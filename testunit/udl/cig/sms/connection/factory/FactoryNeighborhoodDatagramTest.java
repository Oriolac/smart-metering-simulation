package udl.cig.sms.connection.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryNeighborhoodDatagramTest {

    static LoadCurve loadCurve;
    static FactoryNeighborhoodDatagram factory;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        factory = new FactoryNeighborhoodDatagram(loadCurve);
    }

    @Test
    void buildDatagram() {
        byte[] bytes = new byte[factory.getByteSize()];
        byte[] bGenerator = generatorToBytes();
        System.arraycopy(bGenerator, 0, bytes, 0, bGenerator.length);
        String emptyString = "";
        System.arraycopy(emptyString.getBytes(), 0, bytes, bGenerator.length, emptyString.getBytes().length);
        assertEquals(new NeighborhoodDatagram<>(loadCurve.getGroup().getGenerator(), ""),
                factory.buildDatagram(bytes));
    }

    static byte[] generatorToBytes() {
        byte[] bytes = new byte[factory.getByteSize()];
        byte[] x = loadCurve.getGroup().getGenerator().getIntValue().toByteArray();
        byte[] y = loadCurve.getGroup().getGenerator().getY().getIntValue().toByteArray();
        System.arraycopy(x, 0, bytes, 0, x.length);
        System.arraycopy(y, 0, bytes, 192 / 8, y.length);
        return bytes;
    }

    @Test
    void getByteSize() {
        assertEquals(50, factory.getByteSize());
    }
}