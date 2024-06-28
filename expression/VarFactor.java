package expression;

import gather.Poly;
import gather.Monomial;
import java.math.BigInteger;

public class VarFactor extends Factor {
    private final String var;

    public VarFactor(String var) {
        this.var = var;
    }

    @Override
    public Poly toPoly() {
        Monomial monomial = new Monomial(BigInteger.ONE, getExponent(), new Poly());
        Poly poly = new Poly();
        poly.addMonomial(monomial);
        return poly;
    }

    @Override
    public String toString() {
        if (getExponent().equals(BigInteger.ONE)) {
            return var;
        }
        return var + "^" + getExponent().toString();
    }

    @Override
    public String derivation() {
        if (getExponent().equals(BigInteger.ONE)) {
            return "1";
        }
        return getExponent().toString() + "*" + var + "^" + getExponent().subtract(BigInteger.ONE);
    }
}
