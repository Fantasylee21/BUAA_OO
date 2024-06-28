package expression;

import gather.Poly;
import gather.Monomial;
import java.util.ArrayList;

public class Term {

    private final ArrayList<Factor> factors;
    private boolean isNegative;

    public Term() {
        this.factors = new ArrayList<>();
        this.isNegative = false;
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean trueOrFalse) {
        isNegative = trueOrFalse;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        for (Factor factor : factors) {
            Poly factorPoly = factor.toPoly();
            poly.polyMultiply(factorPoly);
        }
        if (isNegative()) {
            for (Monomial m : poly.getMonomials()) {
                m.setCoefficient(m.getCoefficient().negate());
            }
        }
        return poly;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isNegative()) {
            sb.append("-");
        }
        for (int i = 0; i < factors.size(); i++) {
            if (i != 0) {
                sb.append("*");
            }
            sb.append(factors.get(i).toString());
        }
        return sb.toString();
    }

    public String derivation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < factors.size(); i++) {
            if (isNegative()) {
                sb.append("-");
            }
            if (i != 0) {
                if (!isNegative()) {
                    sb.append("+");
                }
            }
            sb.append(factors.get(i).derivation());
            for (int j = 0; j < factors.size(); j++) {
                if (j != i) {
                    sb.append("*");
                    sb.append(factors.get(j).toString());
                }
            }
        }
        if (factors.size() > 1) {
            return "(" + sb + ")";
        }
        return sb.toString();
    }
}
