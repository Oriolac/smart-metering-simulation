package cat.udl.cig.sms.consumption;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class ConsumptionFileReader implements ConsumptionReader{

    private final BufferedReader file;

    public ConsumptionFileReader(BufferedReader file) {
        this.file = file;
    }

    @Override
    public BigInteger read() throws IOException {
        return new BigInteger(file.readLine());
    }
}
