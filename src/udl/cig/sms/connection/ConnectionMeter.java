package udl.cig.sms.connection;

import com.moandjiezana.toml.Toml;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionMeter implements Receiver, Sender {

    private final DataOutputStream out;
    private final DataInputStream in;
    Socket socket;

    public ConnectionMeter(File file) throws IOException {
        socket = tomlToSocket(file);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
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
        /* byte[] bytes = new byte[factory.getByteSize()];
        in.read(bytes);
        return factory.buildDatagram(bytes);

         */
        return null;
    }

    @Override
    public void send(SMSDatagram data) {

    }
}
