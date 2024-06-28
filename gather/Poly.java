package gather;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Poly {
    private ArrayList<Monomial> monomials;
    private final TreeMap<BigInteger, HashMap<Poly, BigInteger>> polyMap;

    public Poly() {
        this.monomials = new ArrayList<>();
        this.polyMap = new TreeMap<>(Comparator.reverseOrder());
    }

    public ArrayList<Monomial> getMonomials() {
        return this.monomials;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Poly) {
            Poly poly = (Poly) obj;
            if (poly.monomials.size() != this.monomials.size()) {
                return false;
            }
            for (int i = 0; i < this.monomials.size(); i++) {
                if (!this.monomials.get(i).equals(poly.monomials.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return monomials.hashCode();
    }

    public void addMonomial(Monomial monomial) {
        for (Monomial m : monomials) {
            if (m.getExponent().equals(monomial.getExponent())) {
                if (m.getPoly().getMonomials().isEmpty()
                        && monomial.getPoly().getMonomials().isEmpty()) {
                    m.setCoefficient(m.getCoefficient().add(monomial.getCoefficient()));
                    return;
                }
                if (!m.getPoly().getMonomials().isEmpty()
                        && !monomial.getPoly().getMonomials().isEmpty()
                        && m.getPoly().equals(monomial.getPoly())) {
                    m.setCoefficient(m.getCoefficient().add(monomial.getCoefficient()));
                    return;
                }
            }
        }
        monomials.add(monomial);
    }

    public void polyAdd(Poly poly) {
        for (Monomial m : poly.monomials) {
            this.addMonomial(m);
        }
    }

    public void addMonomialInMultiply(Poly poly,Monomial monomial) {
        for (Monomial m : poly.monomials) {
            if (m.getExponent().equals(monomial.getExponent())) {
                if (m.getPoly().getMonomials().isEmpty()
                        && monomial.getPoly().getMonomials().isEmpty()) {
                    m.setCoefficient(m.getCoefficient().add(monomial.getCoefficient()));
                    return;
                }
                if (!m.getPoly().getMonomials().isEmpty()
                        && !monomial.getPoly().getMonomials().isEmpty()
                        && m.getPoly().equals(monomial.getPoly())) {
                    m.setCoefficient(m.getCoefficient().add(monomial.getCoefficient()));
                    return;
                }
            }
        }
        poly.monomials.add(monomial);
    }

    public Poly polyAddInMultiply(Poly poly1, Poly poly2) {
        Poly result = new Poly();
        for (Monomial m : poly1.monomials) {
            addMonomialInMultiply(result, m);
        }
        for (Monomial m : poly2.monomials) {
            addMonomialInMultiply(result, m);
        }
        return result;
    }

    public void polyMultiply(Poly poly) {
        Poly result = new Poly();
        if (this.monomials.isEmpty()) {
            this.monomials = poly.monomials;
            return;
        }
        for (Monomial m1 : this.monomials) {
            for (Monomial m2 : poly.monomials) {
                if (m1.getPoly().getMonomials().isEmpty()
                        && m2.getPoly().getMonomials().isEmpty()) {
                    result.addMonomial(new
                            Monomial(m1.getCoefficient().multiply(m2.getCoefficient()),
                            m1.getExponent().add(m2.getExponent()), new Poly()));
                    continue;
                }
                if (m1.getPoly().getMonomials().isEmpty()) {
                    result.addMonomial(new
                            Monomial(m1.getCoefficient().multiply(m2.getCoefficient()),
                            m1.getExponent().add(m2.getExponent()), m2.getPoly()));
                    continue;
                }
                if (m2.getPoly().getMonomials().isEmpty()) {
                    result.addMonomial(new
                            Monomial(m1.getCoefficient().multiply(m2.getCoefficient()),
                            m1.getExponent().add(m2.getExponent()), m1.getPoly()));
                    continue;
                }
                //直接改变了m1.getPoly()的值会影响后面的值，所以要新建一种深拷贝运算 !!!
                Poly addAns = polyAddInMultiply(m1.getPoly(), m2.getPoly());
                result.addMonomial(new Monomial(m1.getCoefficient().multiply(m2.getCoefficient()),
                        m1.getExponent().add(m2.getExponent()), addAns));
            }
        }
        this.monomials = result.monomials;
    }

    public void Construct() {
        for (Monomial m : monomials) {
            if (polyMap.isEmpty()) {
                HashMap<Poly, BigInteger> temp = new HashMap<>();
                temp.put(m.getPoly(), m.getCoefficient());
                polyMap.put(m.getExponent(), temp);
                continue;
            }
            if (polyMap.containsKey(m.getExponent())) {
                if (polyMap.get(m.getExponent()).containsKey(m.getPoly())) {
                    polyMap.get(m.getExponent()).put(m.getPoly(),
                            polyMap.get(m.getExponent()).get(m.getPoly()).add(m.getCoefficient()));
                } else {
                    polyMap.get(m.getExponent()).put(m.getPoly(), m.getCoefficient());
                }
            } else {
                HashMap<Poly, BigInteger> temp = new HashMap<>();
                temp.put(m.getPoly(), m.getCoefficient());
                polyMap.put(m.getExponent(), temp);
            }
        }
    }

    public String toAnswer() {
        StringBuilder sb = new StringBuilder();
        Construct();
        for (BigInteger exp : polyMap.keySet()) {
            for (Poly p : polyMap.get(exp).keySet()) {
                BigInteger coe = polyMap.get(exp).get(p);
                if (coe.equals(BigInteger.ZERO)) {
                    continue;
                }
                if (sb.length() > 0 && coe.compareTo(BigInteger.ZERO) > 0) {
                    sb.append("+");
                }
                if (exp.equals(BigInteger.ZERO)) {
                    if (coe.equals(BigInteger.ONE)) {
                        if (!p.getMonomials().isEmpty()) {
                            String temp = p.toAnswer();
                            if (temp.equals("0")) {
                                sb.append("1");
                                continue;
                            }
                            appendExp(sb, temp);
                            continue;
                        }
                    }
                    sb.append(coe);
                } else if (exp.equals(BigInteger.ONE)) {
                    expOne(coe, sb);
                } else {
                    expElse(coe, exp, sb);
                }
                if (!p.getMonomials().isEmpty()) {
                    String temp = p.toAnswer();
                    if (temp.equals("0")) {
                        if (sb.length() != 0) {
                            continue;
                        }
                        sb.append("*1");
                        continue;
                    }
                    sb.append("*");
                    appendExp(sb, temp);
                }
            }
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        return sb.toString();
    }

    public void expOne(BigInteger coe,StringBuilder sb) {
        if (coe.equals(BigInteger.ONE)) {
            sb.append("x");
        } else if (coe.equals(BigInteger.valueOf(-1))) {
            sb.append("-x");
        } else {
            sb.append(coe).append("*x");
        }
    }

    public void expElse(BigInteger coe,BigInteger exp,StringBuilder sb) {
        if (coe.equals(BigInteger.ONE)) {
            sb.append("x^").append(exp);
        } else if (coe.equals(BigInteger.valueOf(-1))) {
            sb.append("-x^").append(exp);
        } else {
            sb.append(coe).append("*x^").append(exp);
        }
    }

    public boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            //s[0]可以为-
            if (i == 0 && s.charAt(i) == '-') {
                continue;
            }
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public void appendExp(StringBuilder sb, String temp) {
        if (temp.equals("x") || isNumber(temp)) {
            sb.append("exp(").append(temp).append(")");
        } else {
            sb.append("exp((").append(temp).append("))");
        }
    }
}
