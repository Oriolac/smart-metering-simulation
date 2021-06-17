package cat.udl.cig.sms.connection;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Loads socket from TOML file. It only contains staic methods
 */
public class SocketReader {

    /**
     * Loads TOML file. Makes a socket to connect to server
     *
     * @param fileMeter file containing paremeters to connect to socket
     * @return Socket
     * @throws IOException if socket does not exist
     */
    public static Socket tomlToSocket(File fileMeter) throws IOException {
        Toml substToml = new Toml().read(fileMeter);
        String host = substToml.getString("ip");
        InetAddress substAddress = InetAddress.getByName(host);
        int port = substToml.getLong("port").intValue();
        return new Socket(substAddress, port);
    }

    /**
     * Loads toml file to server socket.
     *
     * @param fileSubstation file containing paremeters to create a Serversocket
     * @return ServerSocket
     * @throws IOException if socket does not exist
     */
    public static ServerSocket tomlToServerSocket(File fileSubstation) throws IOException {
        Toml substToml = new Toml().read(fileSubstation);
        int port = substToml.getLong("port").intValue();
        int backlog = substToml.getLong("meters").intValue();
        return new ServerSocket(port, backlog);
    }

    /**
     * Gets the number of meters from the substation file
     *
     * @param fileSubstation file containing the number of meters
     * @return number of meters
     */
    public static int getNumberOfMeters(File fileSubstation) {
        Toml substToml = new Toml().read(fileSubstation);
        return substToml.getLong("meters").intValue();
    }
}
