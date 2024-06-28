package expression;

import gather.Poly;

import java.math.BigInteger;

public class ExprFactor extends Factor {

    private final Expr expr;

    public ExprFactor(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Poly toPoly() {
        BigInteger exponent = getExponent();
        if (exponent.equals(BigInteger.ONE)) {
            return expr.toPoly();
        }
        Poly poly = new Poly();
        Poly exprPoly = expr.toPoly();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(exponent) < 0; i = i.add(BigInteger.ONE)) {
            poly.polyMultiply(exprPoly);
        }
        return poly;
    }

    @Override
    public String toString() {
        if (getExponent().equals(BigInteger.ONE)) {
            return "(" + expr.toString() + ")";
        }
        return "(" + expr.toString() + ")^" + getExponent().toString();
    }

    @Override
    public String derivation() {
        if (getExponent().equals(BigInteger.ONE)) {
            return "(" + expr.derivation() + ")";
        }
        return "(" + expr.derivation() + ")*(" + expr
                + ")^" + getExponent().subtract(BigInteger.ONE) + "*" + getExponent();
    }
}
