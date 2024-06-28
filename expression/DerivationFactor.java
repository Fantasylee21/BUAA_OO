package expression;

import analysis.Lexer;
import analysis.Parser;
import definefunction.FunctionList;
import gather.Poly;
import pre.Treatment;

public class DerivationFactor extends Factor {
    private final Expr expr;

    public DerivationFactor(Expr expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "dx(" + expr.toString() + ")";
    }

    @Override
    public String derivation() {
        String temp =  "(" + expr.derivation() + ")";
        Treatment treatment = new Treatment(temp);
        temp = treatment.preTreatment();
        Lexer lexer = new Lexer(temp);
        Parser parser = new Parser(lexer,new FunctionList());
        Expr expr1 = parser.parseExpr();
        return "(" + expr1.derivation() + ")";
    }

    @Override
    public Poly toPoly() {
        String derivedFunction = this.expr.derivation();
        Treatment treatment = new Treatment(derivedFunction);
        derivedFunction = treatment.preTreatment();
        Lexer lexer = new Lexer(derivedFunction);
        Parser parser = new Parser(lexer,new FunctionList());
        Expr expr = parser.parseExpr();
        return expr.toPoly();
    }
}
