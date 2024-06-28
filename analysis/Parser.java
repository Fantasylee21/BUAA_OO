package analysis;

import definefunction.FunctionList;

import expression.Expr;
import expression.Factor;
import expression.Term;
import expression.NumFactor;
import expression.VarFactor;
import expression.ExprFactor;
import expression.ExpFuncFactor;
import expression.DefineFuncFactor;
import expression.DerivationFactor;

import java.math.BigInteger;
import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;
    private final FunctionList functionList;

    public Parser(Lexer lexer, FunctionList functionList) {
        this.lexer = lexer;
        this.functionList = functionList;
    }

    public Expr parseExpr()
    {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());
        while (lexer.hasNext() && (lexer.peek().isAddOrSub())) {
            lexer.move();
            //处理项的符号
            boolean isPositive = true;
            if (lexer.peek().isAddOrSub()) {
                if (lexer.peek().getType().equals(Token.Type.SUB)
                        && lexer.last().getType().equals(Token.Type.ADD)) {
                    isPositive = false;
                } else if (lexer.peek().getType().equals(Token.Type.ADD)
                        && lexer.last().getType().equals(Token.Type.SUB)) {
                    isPositive = false;
                }
                lexer.move();
            } else {
                if (lexer.last().getType().equals(Token.Type.SUB)) {
                    isPositive = false;
                }
            }
            if (!lexer.hasNext()) {
                break;
            }
            Term term = parseTerm();
            term.setNegative(!isPositive);
            expr.addTerm(term);
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        addFactorByExp(term);
        while (lexer.hasNext() && lexer.peek().getType().equals(Token.Type.MUL)) {
            lexer.move();
            addFactorByExp(term);
        }
        return term;
    }

    public void addFactorByExp(Term term) {
        Factor factor = parseFactor();
        if (factor.getExponent().equals(BigInteger.ZERO)) {
            term.addFactor(new NumFactor(BigInteger.ONE));
            return;
        }
        term.addFactor(factor);
    }

    public void setFactorExponent(Factor factor) {
        if (!lexer.hasNext()) {
            return;
        }
        if (lexer.peek().getType().equals(Token.Type.EXP)) {
            lexer.move();
            if (lexer.peek().getType().equals(Token.Type.NUM)) {
                BigInteger num = new BigInteger(lexer.peek().getContent());
                lexer.move();
                factor.setExponent(num);
            } else {
                throw new RuntimeException("Invalid token");
            }
        }
    }

    public Factor parseFactor() //常数因子暂无指数
    {
        if (lexer.peek().getType().equals(Token.Type.NUM)) {
            return parseNumFactor();
        } else if (lexer.peek().getType().equals(Token.Type.LPAREN)) {
            return parseExprFactor();
        } else if (lexer.isVar()) {
            return parseVarFactor();
        } else if (lexer.peek().getType().equals(Token.Type.EXPFUNC)) {
            return parseExpFuncFactor();
        } else if (lexer.isDefineFunction()) {
            return parseDefineFuncFactor();
        } else if (lexer.peek().getType().equals(Token.Type.DERIVATION)) {
            return parseDerivationFactor();
        } else {
            throw new RuntimeException("Invalid token");
        }
    }

    public Factor parseNumFactor() {
        BigInteger num = new BigInteger(lexer.peek().getContent());
        lexer.move();
        return new NumFactor(num);
    }

    public Factor parseExprFactor() {
        lexer.move();
        ExprFactor exprFactor = new ExprFactor(parseExpr());
        lexer.move();
        setFactorExponent(exprFactor);
        return exprFactor;
    }

    public Factor parseVarFactor() {
        String var = lexer.peek().getContent();
        lexer.move();
        VarFactor varFactor = new VarFactor(var);
        setFactorExponent(varFactor);
        return varFactor;
    }

    public Factor parseExpFuncFactor() {
        lexer.move();
        if (lexer.peek().getType().equals(Token.Type.LPAREN)) {
            lexer.move();
            Expr expr = parseExpr();
            if (lexer.peek().getType().equals(Token.Type.RPAREN)) {
                lexer.move();
                ExpFuncFactor expFuncFactor = new ExpFuncFactor(expr);
                setFactorExponent(expFuncFactor);
                return expFuncFactor;
            } else {
                throw new RuntimeException("Invalid Expression.ExpFuncFactor token");
            }
        } else {
            throw new RuntimeException("Invalid Expression.ExpFuncFactor token");
        }
    }

    //f(1,1,1)
    public Factor parseDefineFuncFactor() {
        lexer.move();
        char name = lexer.last().getContent().charAt(0);
        lexer.move();
        Expr expr1 = parseExpr();
        ArrayList<Expr> parameters = new ArrayList<>();
        parameters.add(expr1);
        DefineFuncFactor defineFuncFactor = new DefineFuncFactor(name, parameters, functionList);
        if (lexer.peek().getType().equals(Token.Type.COMMA)) {
            lexer.move();
            Expr expr2 = parseExpr();
            defineFuncFactor.getParameters().add(expr2);
            if (lexer.peek().getType().equals(Token.Type.COMMA)) {
                lexer.move();
                Expr expr3 = parseExpr();
                defineFuncFactor.getParameters().add(expr3);
            }
        }
        lexer.move();
        return defineFuncFactor;
    }

    public Factor parseDerivationFactor() {
        lexer.move();
        lexer.move();
        DerivationFactor derivationFactor = new DerivationFactor(parseExpr());
        lexer.move();
        return derivationFactor;
    }
}
