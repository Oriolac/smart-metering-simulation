package udl.cig.sms.crypt;

import cat.udl.cig.cryptography.cryptosystems.ElGamal;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import com.moandjiezana.toml.Toml;
import javafx.util.Pair;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class CypherMessage implements Cypher{

    ElGamal elgamal;

    public CypherMessage(File file) {
        //TODO: @sergi @oriol FERHO EN FUNCIONS I TENIR ELGAMAL FET
        Toml toml = new Toml().read(file);
        BigInteger module = new BigInteger(toml.getString("module"));
        BigInteger n = new BigInteger(toml.getString("n"));
        BigInteger b = new BigInteger(toml.getString("b"));
        BigInteger gx = new BigInteger(toml.getString("gx")
                .replaceAll("\\s", ""), 16);
        BigInteger gy = new BigInteger(toml.getString("gy")
                .replaceAll("\\s", ""), 16);

        PrimeField field = new PrimeField(module);

    }



    @Override
    public GeneralECPoint encrypt(BigInteger message) {
        return null;
    }

    @Override
    public List<Pair<BigInteger, Integer>> generateSij() {
        return null;
    }
}
