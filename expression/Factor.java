package expression;

import gather.Poly;
import java.math.BigInteger;

public class Factor {
    private BigInteger exponent = BigInteger.ONE;

    public Factor() {

    }

    public void setExponent(BigInteger exponent) {
        this.exponent = exponent;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }

    public Poly toPoly() {
        return new Poly();
    }

    @Override
    public String toString() {
        return null;
    }

    public String derivation() {
        return null;
    }
}
