package udl.cig.sms.connection.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryGroupElementDatagramTest {

    private static FactoryGroupElementDatagram factory;
    private static LoadCurve loadCurve;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        factory = new FactoryGroupElementDatagram(loadCurve);
    }

    @Test
    void buildDatagram() {
        assertEquals(new GroupElementDatagram(loadCurve.getGroup().getGenerator()),
                factory.buildDatagram(generatorToBytes()));
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
        assertEquals(192 * 2/8, factory.getByteSize());
    }
}