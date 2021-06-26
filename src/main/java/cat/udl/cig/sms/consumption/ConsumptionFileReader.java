package cat.udl.cig.sms.consumption;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import static java.lang.Integer.parseInt;

public class ConsumptionFileReader implements ConsumptionReader{

    private final BufferedReader file;

    public ConsumptionFileReader(BufferedReader file) {
        this.file = file;
    }

    @Override
    public BigInteger read() throws IOException {
        String line = file.readLine();
        long value = Float.valueOf(line).longValue();
        return BigInteger.valueOf(value);
    }
}
