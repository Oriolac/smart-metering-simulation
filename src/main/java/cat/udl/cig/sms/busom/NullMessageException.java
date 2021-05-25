package cat.udl.cig.sms.busom;

/**
 * Exception if the message is null.
 */
public class NullMessageException extends Exception {

    /**
     * Genereates Exception.
     */
    public NullMessageException() {
        super();
    }

    /**
     * Generates excetion with message.
     *
     * @param msg set of the exception.
     */
    public NullMessageException(String msg) {
        super(msg);
    }
}
