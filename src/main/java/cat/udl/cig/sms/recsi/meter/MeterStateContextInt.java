package cat.udl.cig.sms.recsi.meter;

import cat.udl.cig.sms.busom.NullMessageException;

import java.io.IOException;

public interface MeterStateContextInt {

    void establishKey() throws IOException, NullMessageException;
    void sendConsumption() throws IOException, NullMessageException;
    void closeConnection() throws IOException;

}
