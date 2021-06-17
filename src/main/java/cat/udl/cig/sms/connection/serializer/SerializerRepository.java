package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.sms.connection.Datagrams;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initialize the different constructors of the datagrams.
 */
public class SerializerRepository {

    private SMSDatagramSerializer[] serializers = new SMSDatagramSerializer[Datagrams.values().length];
    private static SerializerRepository factory = null;

    public static SerializerRepository getSerializerRepository(CurveConfiguration loadcurve) {
        if (factory == null) {
            factory = new SerializerRepository(loadcurve);
        }
        return factory;
    }

    private SerializerRepository(CurveConfiguration loadCurve) {
        constructSerializers(loadCurve);
    }

    /**
     * @param loadcurve to get the information of the ECC
     */
    private void constructSerializers(CurveConfiguration loadcurve) {
        serializers[Datagrams.CIPHER_TEXT_DATAGRAM.ordinal()] = new CipherTextDatagramSerializer(loadcurve);
        serializers[Datagrams.END_OF_DATAGRAM.ordinal()] = new EndOfDatagramSerializer();
        serializers[Datagrams.GROUP_ELEMENT_DATAGRAM.ordinal()] = new GroupElementDatagramSerializer(loadcurve);
        serializers[Datagrams.NEIGHBORHOOD_DATAGRAM.ordinal()] = new NeighborhoodDatagramSerializer(loadcurve);
        serializers[Datagrams.BIG_INTEGER_DATAGRAM.ordinal()] = new BigIntegerDatagramSerializer();
    }

    /**
     * @param in        needed to read the bytes of the datagram
     * @return SMSDatagram built from the bytes of the input
     * @throws IOException in case that the reads more than it has to.
     */
    public SMSDatagram buildDatagramFromInput(InputStream in) throws IOException {
        byte[] bytes = new byte[1];
        if (in.read(bytes) < bytes.length)
            throw new IOException();
        SMSDatagramSerializer factory = this.serializers[bytes[0]];
        bytes = new byte[factory.getByteSize()];
        if (in.read(bytes) < bytes.length)
            throw new IOException();
        return factory.fromBytes(bytes);
    }
}
