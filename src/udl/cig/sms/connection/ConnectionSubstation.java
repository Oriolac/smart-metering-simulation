package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.connection.factory.FactoryConstructor;
import udl.cig.sms.connection.factory.FactorySMSDatagram;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.data.LoadSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ConnectionSubstation implements ConnectionSubstationInt {


    private final ServerSocket serverSocket;
    private final LoadCurve loadCurve;
    private final int numberOfMeters;
    private final List<Socket> socketList;
    private final FactorySMSDatagram[] factories;

    public ConnectionSubstation(File file, LoadCurve loadCurve) throws IOException {
        this.serverSocket = LoadSocket.tomlToServerSocket(file);
        this.loadCurve = loadCurve;
        this.factories = FactoryConstructor.constructFactories(loadCurve);
        this.numberOfMeters = LoadSocket.getNumberOfMeters(file);
        this.socketList = acceptSockets();
    }

    protected ConnectionSubstation(ServerSocket serverSocket, int numberOfMeters, LoadCurve loadCurve) throws IOException {
        this.serverSocket = serverSocket;
        this.loadCurve = loadCurve;
        this.factories = FactoryConstructor.constructFactories(loadCurve);
        this.numberOfMeters = numberOfMeters;
        this.socketList = acceptSockets();
    }

    private List<Socket> acceptSockets() throws IOException {
        ArrayList<Socket> result = new ArrayList<>();
        for (int i = 0; i < numberOfMeters; ++i) {
            result.add(this.serverSocket.accept());
        }
        return result;
    }

    @Override
    public List<SMSDatagram> receive() throws IOException {
        List<SMSDatagram> datas = new ArrayList<>();
        for(Socket socket : socketList) {
            InputStream in = socket.getInputStream();
            SMSDatagram data = FactoryConstructor.buildDatagramFrom(in, factories);
            datas.add(data);
        }
        return datas;
    }

    @Override
    public void send(SMSDatagram data) throws IOException {
        for(Socket socket : socketList) {
            OutputStream out = socket.getOutputStream();
            out.write(data.toByteArray());
        }
    }

    @Override
    public int getNumberOfMeters() {
        return numberOfMeters;
    }
}
