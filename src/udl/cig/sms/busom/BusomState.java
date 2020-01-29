package udl.cig.sms.busom;

import java.io.IOException;

public interface BusomState {
    BusomState next() throws NullMessageException, IOException;
}
