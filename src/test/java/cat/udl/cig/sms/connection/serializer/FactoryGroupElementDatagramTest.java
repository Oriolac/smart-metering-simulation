package cat.udl.cig.sms.connection.serializer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryGroupElementDatagramTest {

    private static GroupElementDatagramSerializer factory;
    private static CurveConfiguration curveConfiguration;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        factory = new GroupElementDatagramSerializer(curveConfiguration);
    }

    @Test
    void buildDatagram() {
        assertEquals(new GroupElementDatagram(curveConfiguration.getGroup().getGenerator()),
                factory.fromBytes(generatorToBytes()));
    }

    static byte[] generatorToBytes() {
        byte[] bytes = new byte[factory.getByteSize()];
        byte[] x = curveConfiguration.getGroup().getGenerator().getIntValue().toByteArray();
        byte[] y = curveConfiguration.getGroup().getGenerator().getY().getIntValue().toByteArray();
        System.arraycopy(x, 0, bytes, bytes.length / 2 - x.length, x.length);
        System.arraycopy(y, 0, bytes, bytes.length - y.length, y.length);
        return bytes;
    }


    @Test
    void getByteSize() {
        assertEquals(200 * 2/8, factory.getByteSize());
    }
}