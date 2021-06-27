package cat.udl.cig.sms.main;

import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.operations.wrapper.BruteForce;
import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

public class DiscreteLogarithmCostWriter {

    private static final int MAX_BIT_TIMES = 500;
    private static final CurveConfiguration curveConfiguration = CurveConfiguration.P192();
    private static final int MAX_BIT_LENGTH = 26;
    
    private static BigInteger randomMessage(int length) {
        return new BigInteger(length, new SecureRandom());
    }
    
    private static void writingCosts(LogarithmAlgorithm logarithmAlgorithm, String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("analysis/algorithms/" + name + ".csv"));
        writer.write("bitlength,timedelta");
        writer.newLine();
        for(int i = 3; i < MAX_BIT_LENGTH; i++) {
            for (int j = 0; j < MAX_BIT_TIMES; j++ ) {
                GroupElement ciphered = curveConfiguration.getGroup().getGenerator().pow(randomMessage(i));
                long before = Instant.now().toEpochMilli();
                Optional<BigInteger> message = logarithmAlgorithm.algorithm(ciphered);
                if (message.isEmpty())
                    System.out.println("ERROR");
                long after = Instant.now().toEpochMilli();
                writer.write(i + "," + (after - before));
                writer.newLine();
            }
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        LogarithmAlgorithm logarithmAlgorithm = new BruteForce(curveConfiguration.getGroup().getGenerator());
        //writingCosts(logarithmAlgorithm, "brute");
        logarithmAlgorithm = new PollardsLambda(curveConfiguration.getGroup().getGenerator());
        //writingCosts(logarithmAlgorithm, "pollards");
        for (int i = 20; i < MAX_BIT_LENGTH; i++) {
            long before = Instant.now().toEpochMilli();
            HashedAlgorithm.loadHashedInstance(CurveConfiguration.P192().getGroup().getGenerator(),
                    BigInteger.TWO.pow(i), BigInteger.TWO.pow(5));
            long after = Instant.now().toEpochMilli();
            System.out.println(after - before);
        }
        logarithmAlgorithm = HashedAlgorithm.getHashedInstance();
        writingCosts(logarithmAlgorithm, "hashed");
    }
}
