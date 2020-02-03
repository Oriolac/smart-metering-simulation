package udl.cig.sms.connection.datagram;

import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.factory.FactoryGroupElementDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupElementDatagramTest {

    private static LoadCurve loadCurve;
    private static GeneralECPoint gen;
    private static GroupElementDatagram data;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        gen = loadCurve.getGroup().getGenerator();
        data = new GroupElementDatagram(gen);
    }

    @Test
    void toByteArray() {
        GroupElementDatagram expectedData = new GroupElementDatagram(gen);
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(expectedData, new FactoryGroupElementDatagram(loadCurve).buildDatagram(bytes));
    }
}