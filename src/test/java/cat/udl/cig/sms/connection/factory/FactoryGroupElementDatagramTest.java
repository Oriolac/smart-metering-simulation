package sms.connection.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.factory.FactoryGroupElementDatagram;
import udl.cig.sms.data.LoadCurve;

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
        System.arraycopy(x, 0, bytes, bytes.length / 2 - x.length, x.length);
        System.arraycopy(y, 0, bytes, bytes.length - y.length, y.length);
        return bytes;
    }


    @Test
    void getByteSize() {
        assertEquals(200 * 2/8, factory.getByteSize());
    }
}