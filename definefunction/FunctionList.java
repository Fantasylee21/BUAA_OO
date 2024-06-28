package definefunction;

import pre.Treatment;
import analysis.Parser;
import analysis.Lexer;
import expression.Expr;
import java.util.ArrayList;
import java.util.Scanner;

public class FunctionList {
    private final ArrayList<DefineFunction> defineFunctions;

    public FunctionList() {
        this.defineFunctions = new ArrayList<>();
    }

    public ArrayList<DefineFunction> getDefineFunctions() {
        ArrayList<DefineFunction> dfs = new ArrayList<>();
        for (DefineFunction defineFunction : this.defineFunctions) {
            dfs.add(new DefineFunction(defineFunction.getName(), defineFunction.getBody()));
            dfs.get(dfs.size() - 1).setParameters(defineFunction.getParameters());
        }
        return dfs;
    }

    public int readDefineFunction(Scanner scanner)
    {
        int num = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0;i < num;i++)
        {
            int pos = 0;
            String function = scanner.nextLine();
            Treatment treatment = new Treatment(function);
            function = treatment.preTreatment();
            char name = function.charAt(0);
            while (function.charAt(pos) != '=')
            {
                pos++;
            }
            pos++;
            String body = function.substring(pos);
            String replace = body.replace("exp", "e");
            defineFunctions.add(new DefineFunction(name, replace));
            pos = 0;
            while (function.charAt(pos) != ')')
            {
                if (defineFunctions.isEmpty()) {
                    break;
                }
                DefineFunction df = defineFunctions.get(defineFunctions.size() - 1);
                if (function.charAt(pos) == 'x') {
                    defineFunctions.get(defineFunctions.size() - 1).addParameter("u");
                    df.setBody(df.getBody().replace("x", "u"));
                } else if (function.charAt(pos) == 'y') {
                    defineFunctions.get(defineFunctions.size() - 1).addParameter("v");
                    df.setBody(df.getBody().replace("y", "v"));
                } else if (function.charAt(pos) == 'z') {
                    defineFunctions.get(defineFunctions.size() - 1).addParameter("w");
                    df.setBody(df.getBody().replace("z", "w"));
                }
                pos++;
            }
        }
        return num;
    }

    public void replaceDefineFunction() {
        for (int i = 1;i < defineFunctions.size(); i++) {
            DefineFunction df = defineFunctions.get(i);
            String body = df.getBody();
            Treatment treatment = new Treatment(body);
            body = treatment.preTreatment();
            Lexer lexer = new Lexer(body);
            Parser parser = new Parser(lexer, this);
            Expr expr = parser.parseExpr();
            String newBody = "(" + expr.toString() + ")";
            df.setBody(newBody);
        }
    }

    public int defineFunction(Scanner scanner) {
        int num = readDefineFunction(scanner);
        replaceDefineFunction();
        return num;
    }
}
