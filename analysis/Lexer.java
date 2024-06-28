package analysis;

import java.util.ArrayList;

public class Lexer {

    private int cur = 0;
    private int pos = 0;
    private final ArrayList<Token> tokens = new ArrayList<>();

    public String getNumber(String input) {
        char now = input.charAt(pos);
        StringBuilder sb = new StringBuilder();
        if (pos > 1 && input.charAt(pos - 1) == '-' &&
                (input.charAt(pos - 2) == '*' || input.charAt(pos - 2) == '+'
                        || input.charAt(pos - 2) == '-'))
        {
            tokens.remove(tokens.size() - 1);
            sb.append('-');
        }
        //去前缀0
        while (now == '0') {
            pos++;
            if (pos >= input.length()) {
                break;
            }
            now = input.charAt(pos);
        }
        while (now >= '0' && now <= '9') {
            sb.append(now);
            pos++;
            if (pos >= input.length()) {
                break;
            }
            now = input.charAt(pos);
        }
        if (sb.length() == 0 || (sb.length() == 1 && sb.charAt(0) == '-')) {
            sb.append('0');
        }
        return sb.toString();
    }

    public Lexer(String input) {
        cur = 0;
        pos = 0;
        while (pos < input.length()) {
            if (input.charAt(pos) == '(') {
                tokens.add(new Token(Token.Type.LPAREN, "("));
                pos++;
            } else if (input.charAt(pos) == ')') {
                tokens.add(new Token(Token.Type.RPAREN, ")"));
                pos++;
            } else if (input.charAt(pos) == '+') {
                tokens.add(new Token(Token.Type.ADD, "+"));
                pos++;
            } else if (input.charAt(pos) == '-') {
                tokens.add(new Token(Token.Type.SUB, "-"));
                pos++;
            } else if (input.charAt(pos) == '*') {
                tokens.add(new Token(Token.Type.MUL, "*"));
                pos++;
            } else if (input.charAt(pos) == '^') {
                tokens.add(new Token(Token.Type.EXP, "^"));
                pos++;
            } else if (input.charAt(pos) == 'e') {
                tokens.add(new Token(Token.Type.EXPFUNC, "e"));
                pos++;
            } else if (input.charAt(pos) == 'f') {
                tokens.add(new Token(Token.Type.F, "f"));
                pos++;
            } else if (input.charAt(pos) == 'g') {
                tokens.add(new Token(Token.Type.G, "g"));
                pos++;
            } else if (input.charAt(pos) == 'h') {
                tokens.add(new Token(Token.Type.H, "h"));
                pos++;
            } else if (input.charAt(pos) == ',') {
                tokens.add(new Token(Token.Type.COMMA, ","));
                pos++;
            } else if (input.charAt(pos) == 'u' || input.charAt(pos) == 'v' ||
                    input.charAt(pos) == 'w' || input.charAt(pos) == 'x' ||
                    input.charAt(pos) == 'y' || input.charAt(pos) == 'z') {
                varAdd(input);
            } else if (input.charAt(pos) == 'd') {
                tokens.add(new Token(Token.Type.DERIVATION, "d"));
                pos += 2;
            } else if (input.charAt(pos) >= '0' && input.charAt(pos) <= '9') {
                tokens.add(new Token(Token.Type.NUM, getNumber(input)));
            }
        }
    }

    public boolean isDefineFunction() {
        return tokens.get(cur).getType().equals(Token.Type.F) ||
                tokens.get(cur).getType().equals(Token.Type.G) ||
                tokens.get(cur).getType().equals(Token.Type.H);
    }

    public boolean isVar() {
        return tokens.get(cur).getType().equals(Token.Type.U) ||
                tokens.get(cur).getType().equals(Token.Type.V) ||
                tokens.get(cur).getType().equals(Token.Type.W) ||
                tokens.get(cur).getType().equals(Token.Type.X) ||
                tokens.get(cur).getType().equals(Token.Type.Y) ||
                tokens.get(cur).getType().equals(Token.Type.Z);
    }

    public void varAdd(String input) {
        if (input.charAt(pos) == 'u') {
            tokens.add(new Token(Token.Type.U, "u"));
            pos++;
        } else if (input.charAt(pos) == 'v') {
            tokens.add(new Token(Token.Type.V, "v"));
            pos++;
        } else if (input.charAt(pos) == 'w') {
            tokens.add(new Token(Token.Type.W, "w"));
            pos++;
        } else if (input.charAt(pos) == 'x') {
            tokens.add(new Token(Token.Type.X, "x"));
            pos++;
        } else if (input.charAt(pos) == 'y') {
            tokens.add(new Token(Token.Type.Y, "y"));
            pos++;
        } else if (input.charAt(pos) == 'z') {
            tokens.add(new Token(Token.Type.Z, "z"));
            pos++;
        }
    }

    public Token peek()
    {
        return tokens.get(cur);
    }

    public Token last()
    {
        return tokens.get(cur - 1);
    }

    public void move()
    {
        cur++;
    }

    public boolean hasNext()
    {
        return cur < tokens.size();
    }

}
