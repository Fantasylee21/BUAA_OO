package gather;

import java.math.BigInteger;

public class Monomial {
    private BigInteger coefficient;
    private BigInteger exponent;
    private final Poly poly;

    public Monomial(BigInteger coefficient, BigInteger exponent, Poly poly) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.poly = poly;
    }

    public BigInteger getCoefficient() {
        return this.coefficient;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }

    public Poly getPoly() {
        Poly newPoly = new Poly();
        for (Monomial m : poly.getMonomials()) {
            newPoly.addMonomial(new Monomial(m.getCoefficient(), m.getExponent(), m.getPoly()));
        }
        return newPoly;
    }

    public void setCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Monomial) {
            Monomial monomial = (Monomial) obj;
            return this.coefficient.equals(monomial.coefficient)
                    && this.exponent.equals(monomial.exponent)
                    && this.poly.equals(monomial.poly);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return coefficient.hashCode() + exponent.hashCode() + poly.hashCode();
    }
}
