package udl.cig.sms.connection;

import com.moandjiezana.toml.Toml;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.connection.factory.*;
import udl.cig.sms.crypt.LoadCurve;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionMeter implements Receiver, Sender {

    private final DataOutputStream out;
    private final DataInputStream in;
    private Socket socket;
    private FactorySMSDatagram[] factories;

    public ConnectionMeter(File file, LoadCurve loadcurve) throws IOException {
        socket = tomlToSocket(file);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        factories = new FactorySMSDatagram[Datagrams.values().length];
        factories[Datagrams.CIPHER_TEXT_DATAGRAM.ordinal()] = new FactoryCipherTextDatagram(loadcurve);
        factories[Datagrams.END_OF_DATAGRAM.ordinal()] = new FactoryEndOfDatagram();
        factories[Datagrams.GROUP_ELEMENT_DATAGRAM.ordinal()] = new FactoryGroupElementDatagram(loadcurve);
        factories[Datagrams.NEIGHBORHOOD_DATAGRAM.ordinal()] = new FactoryNeighborhoodDatagram(loadcurve);
    }

    public static Socket tomlToSocket(File substation) throws IOException {
        Toml substToml = new Toml().read(substation);
        String host = substToml.getString("ip");
        InetAddress substAddress = InetAddress.getByName(host);
        long port = substToml.getLong("port");
        return new Socket(substAddress, (int) port);
    }

    @Override
    public SMSDatagram receive() throws IOException {
        byte[] bytes = new byte[1];
        in.read(bytes);
        FactorySMSDatagram factory = factories[bytes[0]];
        bytes = new byte[factory.getByteSize()];
        in.read(bytes);
        return factory.buildDatagram(bytes);
    }

    @Override
    public void send(SMSDatagram data) {

    }
}
