package expression;

import gather.Monomial;
import gather.Poly;

import java.math.BigInteger;

public class ExpFuncFactor extends Factor {
    private final Expr expr;

    public ExpFuncFactor(Expr expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        if (getExponent().equals(BigInteger.ONE)) {
            return "e(" + expr.toString() + ")";
        }
        return "e(" + expr.toString() + ")^" + getExponent().toString();
    }

    @Override
    public Poly toPoly() {
        BigInteger size = getExponent();
        Monomial monomial = new Monomial(BigInteger.ONE,BigInteger.ZERO,expr.toPoly());
        Poly poly = new Poly();
        poly.addMonomial(monomial);
        Poly result = new Poly();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(size) < 0; i = i.add(BigInteger.ONE)) {
            result.polyMultiply(poly);
        }
        return result;
    }

    @Override
    public String derivation() {
        if (getExponent().equals(BigInteger.ONE)) {
            return expr.derivation() + "*e(" + expr + ")";
        }
        return "(" + expr.derivation() + ")*" + getExponent() +
                "*e(" + expr + ")^" + getExponent();
    }
}
