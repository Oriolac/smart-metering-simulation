package udl.cig.sms.busom.meter.doubles;

import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.Sender;

public class SenderSpy implements Sender {


    private int count;

    public SenderSpy() {
        this.count = 0;
    }

    @Override
    public void send(SMSDatagram data) {
        count++;
    }

    public int getCount() {
        return count;
    }
}
