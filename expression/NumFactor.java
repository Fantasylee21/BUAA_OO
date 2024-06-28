package expression;

import gather.Monomial;
import gather.Poly;
import java.math.BigInteger;

public class NumFactor extends Factor {
    private final BigInteger num;

    public NumFactor(BigInteger num) {
        this.num = num;
    }

    @Override
    public Poly toPoly() {
        Monomial monomial = new Monomial(num, BigInteger.ZERO,new Poly());
        Poly poly = new Poly();
        poly.addMonomial(monomial);
        return poly;
    }

    @Override
    public String toString() {
        return num.toString();
    }

    @Override
    public String derivation() {
        return "0";
    }
}
