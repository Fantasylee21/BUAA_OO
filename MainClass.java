import analysis.Lexer;
import analysis.Parser;
import definefunction.FunctionList;
import expression.Expr;
import gather.Poly;
import pre.Treatment;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FunctionList functionList = new FunctionList();
        int num = functionList.defineFunction(scanner);
        String input = scanner.nextLine();
        String input1;
        if (num != 0) {
            Treatment treatment = new Treatment(input);
            input = treatment.preTreatment();
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer, functionList);
            Expr expr = parser.parseExpr();
            input1 = expr.toString();
        } else {
            input1 = input;
        }

        Treatment treatment1 = new Treatment(input1);
        input1 = treatment1.preTreatment();
        Lexer lexer1 = new Lexer(input1);
        Parser parser1 = new Parser(lexer1, functionList);
        Expr expr1 = parser1.parseExpr();

        Poly poly = expr1.toPoly();
        String answer = poly.toAnswer();
        System.out.println(answer);
    }

}
