package cat.udl.cig.sms.connection;

public class KeyRenewalException extends Exception{

    public KeyRenewalException() {
    }

    public KeyRenewalException(String message) {
        super(message);
    }

    public KeyRenewalException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyRenewalException(Throwable cause) {
        super(cause);
    }

    public KeyRenewalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
