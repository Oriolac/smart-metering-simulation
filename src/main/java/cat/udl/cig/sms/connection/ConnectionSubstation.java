package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.connection.serializer.SerializerRepository;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the substation's connection using TCP.
 */
public class ConnectionSubstation implements ConnectionSubstationInt {


    private final ServerSocket serverSocket;
    private final int numberOfMeters;
    private final List<Socket> socketList;
    private final SerializerRepository serializerRepository;

    /**
     * @param file      to get the information of the port that must be on.
     * @param curveConfiguration to get the information of the ECC.
     * @throws IOException in case the socket is unavailable.
     */
    public ConnectionSubstation(File file, CurveConfiguration curveConfiguration) throws IOException {
        this.serverSocket = SocketReader.tomlToServerSocket(file);
        this.serializerRepository = SerializerRepository.getSerializerRepository(curveConfiguration);
        this.numberOfMeters = SocketReader.getNumberOfMeters(file);
        this.socketList = acceptSockets();
    }

    /**
     * @param serverSocket   socket of the server
     * @param numberOfMeters capacity of the substation
     * @param curveConfiguration      to get the information of the ECC
     * @throws IOException in case the server cannot accept the sockets.
     */
    public ConnectionSubstation(ServerSocket serverSocket, int numberOfMeters, CurveConfiguration curveConfiguration) throws IOException {
        this.serverSocket = serverSocket;
        this.serializerRepository = SerializerRepository.getSerializerRepository(curveConfiguration);
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
        for (Socket socket : socketList) {
            InputStream in = socket.getInputStream();
            SMSDatagram data = this.serializerRepository.buildDatagramFromInput(in);
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
        for (Socket socket : socketList) {
            OutputStream out = socket.getOutputStream();
            out.write(data.toByteArray());
        }
    }

}
