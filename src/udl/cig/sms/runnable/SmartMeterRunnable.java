package udl.cig.sms.runnable;

import cat.udl.cig.ecc.GeneralECPoint;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.HashMap;

public class SmartMeterRunnable {

    public static void main(String[] args) {
        initVariables(args);
        while (true) {
            //getpackage().then( | );
        }
    }

    private static void initVariables(String[] args) {
        File file = new File(args[1]);
        Toml toml = new Toml().read(file);
        toml.getString("module");


    }

    private BigInteger getItBig(String key) {
        return null;
    }
}
