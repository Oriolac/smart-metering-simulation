package udl.cig.sms.connection.factory;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.RingElement;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.data.LoadCurve;

import java.util.Arrays;

public class FactoryNeighborhoodDatagram implements FactorySMSDatagram {
    private final int LENGTH_CURVE;
    private static final int NUM_RING_ELEMENTS = 2;
    private static final int LENGTH_CERTIFICATE = 8;
    private final LoadCurve loadCurve;

    public FactoryNeighborhoodDatagram(LoadCurve loadCurve) {
        LENGTH_CURVE = loadCurve.getCurve().getCardinalityFactors().get(0).bitLength();
        this.loadCurve = loadCurve;
    }

    @Override
    public NeighborhoodDatagram<String> buildDatagram(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE/8);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE/8, LENGTH_CURVE/8 * 2);
        GroupElement groupElement = new GeneralECPoint(loadCurve.getCurve(), cx, cy);
        return new NeighborhoodDatagram<>(groupElement, "");
    }

    @Override
    public int getByteSize() {
        return (LENGTH_CURVE * NUM_RING_ELEMENTS + LENGTH_CERTIFICATE * Character.BYTES) / 8;
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return loadCurve.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }
}
