package udl.cig.sms.data;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class LoadSocket {

    public static Socket tomlToSocket(File fileSubstation) throws IOException {
        Toml substToml = new Toml().read(fileSubstation);
        String host = substToml.getString("ip");
        InetAddress substAddress = InetAddress.getByName(host);
        int port = substToml.getLong("port").intValue();
        return new Socket(substAddress, port);
    }

    public static ServerSocket tomlToServerSocket(File fileSubstation) throws IOException {
        Toml substToml = new Toml().read(fileSubstation);
        int port = substToml.getLong("port").intValue();
        int backlog = substToml.getLong("meters").intValue();
        return new ServerSocket(port, backlog);
    }

    public static int getNumberOfMeters(File fileSubstation) throws IOException {
        Toml substToml = new Toml().read(fileSubstation);
        return substToml.getLong("meters").intValue();
    }
}
