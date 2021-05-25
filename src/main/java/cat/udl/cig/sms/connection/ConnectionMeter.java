package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.connection.factory.FactoryConstructor;
import cat.udl.cig.sms.connection.factory.FactorySMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;
import cat.udl.cig.sms.data.LoadSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static cat.udl.cig.sms.connection.factory.FactoryConstructor.constructFactories;

/**
 * The meter's connection
 */
public class ConnectionMeter implements ConnectionMeterInt {

    private final DataOutputStream out;
    private final DataInputStream in;
    private Socket socket;
    private FactorySMSDatagram[] factories;

    /**
     * @param file of the configuration of the port and meters
     * @param loadcurve get the information of the ECC
     * @throws IOException in case IO fails
     */
    public ConnectionMeter(File file, LoadCurve loadcurve) throws IOException {
        socket = LoadSocket.tomlToSocket(file);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        factories = constructFactories(loadcurve);
    }

    /**
     * @param in to receive the data
     * @param out to send the data
     * @param loadCurve to have all the information of the ECC
     */
    protected ConnectionMeter(DataInputStream in, DataOutputStream out, LoadCurve loadCurve) {
        this.in = in;
        this.out = out;
        factories = constructFactories(loadCurve);
    }


    /**
     * @return the SMSDatagram that receives
     * @throws IOException in case cannot read as it has to be
     */
    @Override
    public SMSDatagram receive() throws IOException {
        return FactoryConstructor.buildDatagramFrom(in, factories);
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
