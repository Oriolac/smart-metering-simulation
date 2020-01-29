package udl.cig.sms.connection.factory;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.RingElement;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.data.LoadCurve;

import java.util.Arrays;

public class FactoryCipherTextDatagram implements FactorySMSDatagram {

    private final int LENGTH_CURVE;
    private static final int NUM_POINTS = 2 * 2;
    private final LoadCurve loadCurve;

    public FactoryCipherTextDatagram(LoadCurve loadCurve) {
        LENGTH_CURVE = loadCurve.getCurve().getCardinalityFactors().get(0).bitLength();
        this.loadCurve = loadCurve;
    }

    @Override
    public CipherTextDatagram buildDatagram(byte[] bytes) {
        RingElement cx = fromBytes(bytes, 0, LENGTH_CURVE / 8);
        RingElement cy = fromBytes(bytes, LENGTH_CURVE / 8, LENGTH_CURVE / 8 * 2);
        RingElement dx = fromBytes(bytes, LENGTH_CURVE / 8 * 2, LENGTH_CURVE / 8 * 3);
        RingElement dy = fromBytes(bytes, LENGTH_CURVE / 8 * 3, LENGTH_CURVE / 8 * 4);
        GeneralECPoint c = new GeneralECPoint(loadCurve.getCurve(), cx, cy);
        GeneralECPoint d = new GeneralECPoint(loadCurve.getCurve(), dx, dy);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(new GeneralECPoint[]{c, d});
        return new CipherTextDatagram(ciphertext);
    }

    private RingElement fromBytes(byte[] bytes, int from, int to) {
        return loadCurve.getField().fromBytes(Arrays.copyOfRange(bytes, from, to));
    }

    @Override
    public int getByteSize() {
        return (LENGTH_CURVE * NUM_POINTS) / 8;
    }
}
