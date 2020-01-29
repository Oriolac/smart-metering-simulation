package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.connection.factory.FactoryConstructor;
import udl.cig.sms.connection.factory.FactorySMSDatagram;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.data.LoadSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static udl.cig.sms.connection.factory.FactoryConstructor.constructFactories;

public class ConnectionMeter implements ConnectionMeterInt {

    private final DataOutputStream out;
    private final DataInputStream in;
    private Socket socket;
    private FactorySMSDatagram[] factories;

    public ConnectionMeter(File file, LoadCurve loadcurve) throws IOException {
        socket = LoadSocket.tomlToSocket(file);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        factories = constructFactories(loadcurve);
    }

    protected ConnectionMeter(DataInputStream in, DataOutputStream out, LoadCurve loadCurve) {
        this.in = in;
        this.out = out;
        factories = constructFactories(loadCurve);
    }



    @Override
    public SMSDatagram receive() throws IOException {
        return FactoryConstructor.buildDatagramFrom(in, factories);
    }

    @Override
    public void send(SMSDatagram data) throws IOException {
        byte[] bytes = data.toByteArray();
        out.write(bytes);
    }

    public Socket getSocket() {
        return socket;
    }
}
