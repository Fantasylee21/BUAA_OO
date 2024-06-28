package pre;

public class Treatment {
    private String input;

    public Treatment(String input) {
        this.input = input;
    }

    public String delSpace(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (legalChar(input.charAt(i))) {
                sb.append(input.charAt(i));
            }
        }
        return sb.toString();
    }

    public boolean legalChar(char c) {
        boolean isCal = c == '+' || c == '-' || c == '*' || c == '^' || c == ',' || c == '=';
        boolean isLetter = (c >= 'a' && c <= 'z');
        return (c >= '0' && c <= '9') || isCal || c == '(' || c == ')' || isLetter;
    }

    public boolean isCalculation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '^' || c == ',';
    }

    public String adjustExtraSign(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < input.length();i++)
        {
            if (input.charAt(i) == '-' && (sb.length() == 0
                    || sb.charAt(sb.length() - 1) == '(' || sb.charAt(sb.length() - 1) == '-'
                    || sb.charAt(sb.length() - 1) == ',')) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '-') {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append('+');
                    continue;
                } else {
                    sb.append("0");
                }
            } else if (input.charAt(i) == '+'
                    && (sb.length() == 0
                    || sb.charAt(sb.length() - 1) == '('
                    || isCalculation(sb.charAt(sb.length() - 1)))) {
                continue;
            }
            sb.append(input.charAt(i));
        }
        return sb.toString();
    }

    public String preTreatment() {
        this.input = delSpace(input);
        this.input = adjustExtraSign(input);
        this.input = this.input.replace("exp", "e");
        return this.input;
    }
}
