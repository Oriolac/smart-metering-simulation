package udl.cig.sms.connection.datagram;

import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.factory.FactoryNeighborhoodDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeighborhoodDatagramTest {


    private static LoadCurve loadCurve;
    private static GeneralECPoint gen;
    private static NeighborhoodDatagram<String> data;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        gen = loadCurve.getGroup().getGenerator();
        data = new NeighborhoodDatagram<>(gen, "");
    }

    @Test
    void toByteArray() {
        NeighborhoodDatagram<String> expectedData = new NeighborhoodDatagram<>(gen, "");
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(expectedData, new FactoryNeighborhoodDatagram(loadCurve).buildDatagram(bytes));
    }
}