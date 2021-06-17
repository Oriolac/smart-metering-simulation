package cat.udl.cig.sms.connection.serializer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryNeighborhoodDatagramTest {

    static CurveConfiguration curveConfiguration;
    static NeighborhoodDatagramSerializer factory;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        factory = new NeighborhoodDatagramSerializer(curveConfiguration);
    }

    @Test
    void buildDatagram() {
        NeighborhoodDatagram<String> data = new NeighborhoodDatagram<>(curveConfiguration.getGroup().getGenerator(), "");
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(data,
                factory.fromBytes(bytes));
    }

    @Test
    void getByteSize() {
        assertEquals(66, factory.getByteSize());
    }
}