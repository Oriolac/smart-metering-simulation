package udl.cig.sms.busom;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.ConnectionMeter;
import udl.cig.sms.connection.ConnectionSubstation;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.data.LoadSocket;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusomControllersTest {

    private static List<MeterBusomController> meterContrs;
    private static SubstationBusomController substContr;

    private static ConnectionSubstation connectionSubstation;
    private static List<ConnectionMeter> connectionMeters;
    private static File fileSubst;
    private static LoadCurve loadCurve;

    @BeforeAll
    static void setUp() throws IOException, NullMessageException {
        fileSubst = new File("./data/test/substation3.toml");
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        HashedAlgorithm.loadHashedInstance(loadCurve.getGroup().getGenerator(), BigInteger.valueOf(1024 * 1024),
                BigInteger.valueOf(32));

        setUpSockets();
        meterContrs = new ArrayList<>();
        for (ConnectionMeter meter : connectionMeters) {
            meterContrs.add(new MeterBusomController(loadCurve, meter));
        }
        substContr = new SubstationBusomController(loadCurve, connectionSubstation);
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
        ServerSocket server = LoadSocket.tomlToServerSocket(fileSubst);
        int numberOfMeters = LoadSocket.getNumberOfMeters(fileSubst);
        connectionMeters = new ArrayList<>();
        for (int i = 0; i < numberOfMeters; i++)
            connectionMeters.add(new ConnectionMeter(fileSubst, loadCurve));
        connectionSubstation = new ConnectionSubstation(server, numberOfMeters, loadCurve);
    }

    @Test
    void sendMessages() throws IOException, NullMessageException, InterruptedException {
        List<BigInteger> message = getMessage();
        List<Thread> threads = new ArrayList<>();
        for (MeterBusomController meter : meterContrs) {
            threads.add(new Thread(() -> {
                try {
                    meter.meterSetUp();
                    meter.sendMessage(message);
                } catch (IOException | NullMessageException e) {
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