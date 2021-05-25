package cat.udl.cig.sms.connection.factory;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.util.Arrays;

/**
 * Builds the NeighborhoodDatagram
 */
public class FactoryNeighborhoodDatagram implements FactorySMSDatagram {
    private final int LENGTH_CURVE;
    private static final int NUM_RING_ELEMENTS = 2;
    private static final int LENGTH_CERTIFICATE = 8;
    private final LoadCurve loadCurve;

    /**
     * @param loadCurve get the information of the ECC
     */
    public FactoryNeighborhoodDatagram(LoadCurve loadCurve) {
        LENGTH_CURVE = loadCurve.getCurve().getCardinalityFactors().get(0).bitLength() / 8 + 1;
        this.loadCurve = loadCurve;
    }

    /**
     * @param bytes the content in bytes of the Datagram, the point of the ECC
     * @return the NeighborhoodDatagram of String certificate
     */
    @Override
    public NeighborhoodDatagram<String> buildDatagram(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE, LENGTH_CURVE * 2);
        GroupElement groupElement = new GeneralECPoint(loadCurve.getCurve(), cx, cy);
        return new NeighborhoodDatagram<>(groupElement, "");
    }

    /**
     * @return the size of the content of the Datagram
     */
    @Override
    public int getByteSize() {
        return LENGTH_CURVE * NUM_RING_ELEMENTS + LENGTH_CERTIFICATE * Character.BYTES;
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return loadCurve.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }
}
