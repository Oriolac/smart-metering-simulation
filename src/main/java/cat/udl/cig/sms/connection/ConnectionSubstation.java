package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.connection.factory.FactoryConstructor;
import cat.udl.cig.sms.connection.factory.FactorySMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;
import cat.udl.cig.sms.data.LoadSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the substation's connection using TCP.
 */
public class ConnectionSubstation implements ConnectionSubstationInt {


    private final ServerSocket serverSocket;
    private final LoadCurve loadCurve;
    private final int numberOfMeters;
    private final List<Socket> socketList;
    private final FactorySMSDatagram[] factories;

    /**
     * @param file to get the information of the port that must be on.
     * @param loadCurve to get the information of the ECC.
     * @throws IOException in case the socket is unavailable.
     */
    public ConnectionSubstation(File file, LoadCurve loadCurve) throws IOException {
        this.serverSocket = LoadSocket.tomlToServerSocket(file);
        this.loadCurve = loadCurve;
        this.factories = FactoryConstructor.constructFactories(loadCurve);
        this.numberOfMeters = LoadSocket.getNumberOfMeters(file);
        this.socketList = acceptSockets();
    }

    /**
     * @param serverSocket socket of the server
     * @param numberOfMeters capacity of the substation
     * @param loadCurve to get the information of the ECC
     * @throws IOException in case the server cannot accept the sockets.
     */
    public ConnectionSubstation(ServerSocket serverSocket, int numberOfMeters, LoadCurve loadCurve) throws IOException {
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

    /**
     * @return the list of SMSDatagrams received, one per socket
     * @throws IOException in case we cannot read the socket.
     */
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

    /**
     * @param data: the data that we would like to send
     * @throws IOException in case IO fails
     */
    @Override
    public void send(SMSDatagram data) throws IOException {
        for(Socket socket : socketList) {
            OutputStream out = socket.getOutputStream();
            out.write(data.toByteArray());
        }
    }

    /**
     * @return the number of meters that it is declared.
     */
    @Override
    public int getNumberOfMeters() {
        return numberOfMeters;
    }
}
