package analysis;

public class Token {

    public enum Type {
        NUM, X, EXPFUNC, ADD, SUB, MUL, EXP, LPAREN, RPAREN, F, G, H, COMMA, U, V, W, DERIVATION,
        Y, Z
    }

    private final Type type;
    private final String content;

    public Token(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public boolean isAddOrSub() {
        return type.equals(Type.ADD) || type.equals(Type.SUB);
    }
}
