package cat.udl.cig.sms.consumption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConsumptionFileReaderTest {

    private ConsumptionFileReader consumptionReader;
    private final String consumptionString = "83.43";

    @BeforeEach
    void setUp() throws IOException {
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Mockito.when(bufferedReader.readLine()).thenReturn(consumptionString);
        consumptionReader = new ConsumptionFileReader(bufferedReader);
    }

    @Test
    void testBigIntegerReading() throws IOException {
        BigInteger bigInteger = consumptionReader.read();
        BigInteger expected = BigInteger.valueOf(Float.valueOf(consumptionString).longValue());
        assertNotNull(bigInteger);
        assertEquals(expected, bigInteger);
    }
}