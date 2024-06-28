package expression;

import java.util.ArrayList;
import gather.Poly;

public class Expr {
    private final ArrayList<Term> terms;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        for (Term term : terms) {
            Poly termPoly = term.toPoly();
            poly.polyAdd(termPoly);
        }
        return poly;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); i++) {
            if (i != 0) {
                sb.append("+");
            }
            sb.append(terms.get(i).toString());
        }
        return sb.toString();
    }

    public String derivation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); i++) {
            if (i != 0) {
                sb.append("+");
            }
            sb.append(terms.get(i).derivation());
        }
        return sb.toString();
    }

}
