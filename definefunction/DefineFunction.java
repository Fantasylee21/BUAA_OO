package definefunction;

import java.util.ArrayList;

public class DefineFunction {
    private final char name;
    private ArrayList<String> parameters;
    private String body;

    public DefineFunction(char name, String body) {
        this.name = name;
        this.parameters = new ArrayList<>();
        this.body = body;
    }

    public void setParameters(ArrayList<String> parameters) {
        this.parameters = parameters;
    }

    public char getName() {
        return this.name;
    }

    public ArrayList<String> getParameters() {
        return this.parameters;
    }

    public String getBody() {
        return this.body;
    }

    public void addParameter(String parameter) {
        this.parameters.add(parameter);
    }

    public String setBody(String body) {
        return this.body = body;
    }
}
