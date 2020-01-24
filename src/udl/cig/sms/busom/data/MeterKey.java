package udl.cig.sms.busom.data;

import cat.udl.cig.fields.GroupElement;

import java.math.BigInteger;

public final class MeterKey {

    private final GroupElement generalKey;
    private final BigInteger privateKey;

    public MeterKey(BigInteger privateKey, GroupElement generalKey) {
        this.privateKey = privateKey;
        this.generalKey = generalKey;
    }

    public GroupElement getGeneralKey() {
        return generalKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }
}
