package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.util.Arrays;

/**
 * Builds the GroupElementDatagram
 */
public class GroupElementDatagramSerializer implements SMSDatagramSerializer {

    private final int LENGTH_CURVE;
    private static final int NUM_RING_ELEMENTS = 2;
    private final CurveConfiguration curveConfiguration;

    /**
     * @param curveConfiguration to get the information of the ECC
     */
    public GroupElementDatagramSerializer(CurveConfiguration curveConfiguration) {
        LENGTH_CURVE = curveConfiguration.getCurve().getCardinalityFactors().get(0).bitLength() / 8 + 1;
        this.curveConfiguration = curveConfiguration;
    }

    /**
     * @param bytes that represent the the point of the curve in bytes.
     * @return the GroupElementDatagram of the content
     */
    @Override
    public GroupElementDatagram fromBytes(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE, LENGTH_CURVE * 2);
        GroupElement groupElement = new GeneralECPoint(curveConfiguration.getCurve(), cx, cy);
        return new GroupElementDatagram(groupElement);
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return curveConfiguration.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }

    /**
     * @return the size of the content
     */
    @Override
    public int getByteSize() {
        return LENGTH_CURVE * NUM_RING_ELEMENTS;
    }
}
