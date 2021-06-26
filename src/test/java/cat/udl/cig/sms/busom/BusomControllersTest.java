package cat.udl.cig.sms.busom;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.sms.connection.KeyRenewalException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.ConnectionMeter;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.connection.SocketReader;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusomControllersTest {

    private static List<MeterBusomService> meterContrs;
    private static SubstationBusomService substContr;

    private static ConnectionSubstation connectionSubstation;
    private static List<ConnectionMeter> connectionMeters;
    private static File fileSubst;
    private static CurveConfiguration curveConfiguration;

    @BeforeAll
    static void setUp() throws IOException, NullMessageException, KeyRenewalException {
        fileSubst = new File("./data/test/substation3.toml");
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        HashedAlgorithm.loadHashedInstance(curveConfiguration.getGroup().getGenerator(), BigInteger.valueOf(1024 * 1024),
                BigInteger.valueOf(32));

        setUpSockets();
        meterContrs = new ArrayList<>();
        for (ConnectionMeter meter : connectionMeters) {
            meterContrs.add(new MeterBusomService(curveConfiguration, meter));
        }
        substContr = new SubstationBusomService(curveConfiguration, connectionSubstation);
    }

    private static List<BigInteger> getMessage() {
        List<BigInteger> message;
        message = new ArrayList<>();
        message.add(BigInteger.TWO);
        for (int i = 0; i < 14; i++) {
            message.add(BigInteger.ZERO);
        }
        return message;
    }

    static void setUpSockets() throws IOException {
        ServerSocket server = SocketReader.tomlToServerSocket(fileSubst);
        int numberOfMeters = SocketReader.getNumberOfMeters(fileSubst);
        connectionMeters = new ArrayList<>();
        for (int i = 0; i < numberOfMeters; i++)
            connectionMeters.add(new ConnectionMeter(fileSubst, curveConfiguration));
        connectionSubstation = new ConnectionSubstation(server, numberOfMeters, curveConfiguration);
    }

    @Test
    void sendMessages() throws IOException, NullMessageException, InterruptedException {
        List<BigInteger> message = getMessage();
        List<Thread> threads = new ArrayList<>();
        for (MeterBusomService meter : meterContrs) {
            threads.add(new Thread(() -> {
                try {
                    meter.start();
                    meter.sendMessage(message);
                } catch (IOException | NullMessageException | KeyRenewalException e) {
                    e.printStackTrace();
                }
            }));
        }
        for (Thread thread1 : threads) {
            thread1.start();
        }
        BigInteger y = substContr.receiveSecretKey();
        assertEquals(BigInteger.valueOf(6), y);
        for (Thread thread : threads) {
            thread.join();
        }
    }

}