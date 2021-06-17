package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.util.Arrays;

/**
 * Builds the CypherTextDatagram
 */
public class CipherTextDatagramSerializer implements SMSDatagramSerializer {

    private final int LENGTH_CURVE;
    private static final int NUM_POINTS = 2 * 2;
    private final CurveConfiguration curveConfiguration;

    /**
     * @param curveConfiguration to get the information of the ECC
     */
    public CipherTextDatagramSerializer(CurveConfiguration curveConfiguration) {
        LENGTH_CURVE = curveConfiguration.getCurve().getCardinalityFactors().get(0).bitLength() / 8 + 1;
        this.curveConfiguration = curveConfiguration;
    }

    /**
     * @param bytes that represent the Ciphertext as the pair of two GeneralECPoint
     * @return the new CipherTextDatagram constructed.
     */
    @Override
    public CipherTextDatagram fromBytes(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE, LENGTH_CURVE * 2);
        RingElement dx = fromBytes(bytes, LENGTH_CURVE * 2, LENGTH_CURVE * 3);
        RingElement dy = fromBytes(bytes, LENGTH_CURVE * 3, LENGTH_CURVE * 4);
        GeneralECPoint c = new GeneralECPoint(curveConfiguration.getCurve(), cx, cy);
        GeneralECPoint d = new GeneralECPoint(curveConfiguration.getCurve(), dx, dy);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(new GeneralECPoint[]{c, d});
        return new CipherTextDatagram(ciphertext);
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return curveConfiguration.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }

    /**
     * @return the size of the content of the datagram.
     */
    @Override
    public int getByteSize() {
        return LENGTH_CURVE * NUM_POINTS;
    }
}
