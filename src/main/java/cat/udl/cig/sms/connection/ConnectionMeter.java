package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.connection.serializer.SerializerRepository;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * The meter's connection
 */
public class ConnectionMeter implements ConnectionMeterInt {

    private final DataOutputStream out;
    private final DataInputStream in;
    private Socket socket;
    private final SerializerRepository serializerRepository
            ;

    /**
     * @param file      of the configuration of the port and meters
     * @param loadcurve get the information of the ECC
     * @throws IOException in case IO fails
     */
    public ConnectionMeter(File file, CurveConfiguration loadcurve) throws IOException {
        socket = SocketReader.tomlToSocket(file);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        serializerRepository = SerializerRepository.getSerializerRepository(loadcurve);
    }

    /**
     * @param in        to receive the data
     * @param out       to send the data
     * @param curveConfiguration to have all the information of the ECC
     */
    protected ConnectionMeter(DataInputStream in, DataOutputStream out, CurveConfiguration curveConfiguration) {
        this.in = in;
        this.out = out;
        serializerRepository = SerializerRepository.getSerializerRepository(curveConfiguration);
    }


    /**
     * @return the SMSDatagram that receives
     * @throws IOException in case cannot read as it has to be
     */
    @Override
    public SMSDatagram receive() throws IOException {
        return this.serializerRepository.buildDatagramFromInput(in);
    }

    /**
     * @param data: the data that we would like to send
     * @throws IOException in case it does not send correctly the datagram
     */
    @Override
    public void send(SMSDatagram data) throws IOException {
        byte[] bytes = data.toByteArray();
        out.write(bytes);
    }

    /**
     * @return the socket of the connection
     */
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
