package sms.connection.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.connection.factory.FactoryNeighborhoodDatagram;
import udl.cig.sms.data.LoadCurve;

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
        NeighborhoodDatagram<String> data = new NeighborhoodDatagram<>(loadCurve.getGroup().getGenerator(), "");
        byte[] bytes = data.toByteArray();
        System.arraycopy(bytes, 1, bytes, 0, bytes.length - 1);
        assertEquals(data,
                factory.buildDatagram(bytes));
    }

    @Test
    void getByteSize() {
        assertEquals(66, factory.getByteSize());
    }
}