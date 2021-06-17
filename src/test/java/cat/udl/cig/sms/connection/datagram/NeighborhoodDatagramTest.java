package cat.udl.cig.sms.connection.datagram;

import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.serializer.NeighborhoodDatagramSerializer;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeighborhoodDatagramTest {


    private static CurveConfiguration curveConfiguration;
    private static GeneralECPoint gen;
    private static NeighborhoodDatagram<String> data;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        gen = curveConfiguration.getGroup().getGenerator();
        data = new NeighborhoodDatagram<>(gen, "");
    }

    @Test
    void toByteArray() {
        NeighborhoodDatagram<String> expectedData = new NeighborhoodDatagram<>(gen, "");
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(expectedData, new NeighborhoodDatagramSerializer(curveConfiguration).fromBytes(bytes));
    }
}