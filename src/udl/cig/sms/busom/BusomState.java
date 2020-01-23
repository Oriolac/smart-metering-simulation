package udl.cig.sms.busom;

public interface BusomState {
    BusomState next() throws NullMessageException;
}
