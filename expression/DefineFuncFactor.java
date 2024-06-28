package expression;

import definefunction.DefineFunction;
import definefunction.FunctionList;
import java.util.ArrayList;

public class DefineFuncFactor extends Factor {
    private final char name;
    private final ArrayList<Expr> parameters;
    private final FunctionList functionList;

    public DefineFuncFactor(char name, ArrayList<Expr> parameters, FunctionList functionList) {
        this.name = name;
        this.parameters = parameters;
        this.functionList = functionList;
    }

    public ArrayList<Expr> getParameters() {
        return parameters;
    }

    public String toString() {
        for (int i = 0;i < functionList.getDefineFunctions().size();i++) {
            DefineFunction defineFunction = functionList.getDefineFunctions().get(i);
            if (defineFunction.getName() == name) {
                String para = "mnp";
                for (int j = 0; j < parameters.size(); j++) {
                    String body = defineFunction.getBody();
                    String replaced = defineFunction.getParameters().get(j);
                    String temp = body.replace(replaced, para.charAt(j) + "");
                    defineFunction.setBody(temp);
                }
                for (int j = 0; j < parameters.size(); j++) {
                    String replaceExpr = "(" + parameters.get(j).toString() + ")";
                    String body = defineFunction.getBody();
                    //递归是会出现问题的重复替换
                    String temp = body.replace(para.charAt(j) + "", replaceExpr);
                    String ans = "(" + temp + ")";
                    defineFunction.setBody(ans);
                }
                return defineFunction.getBody();
            }
        }
        return "";
    }
}
