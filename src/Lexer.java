import java.util.HashSet;
import java.util.Set;

public class Lexer {
    private String code;
    private int currentPosition;
    private Set<String> keywords;

    public Lexer(String code, String language) {
        this.code = code;
        this.currentPosition = 0;
        this.keywords = new HashSet<>();

        // Agregar palabras clave para cada lenguaje
        switch (language) {
            case "PL/SQL":
                keywords.addAll(Set.of("CREATE", "TABLE", "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR", "NOT", "NULL", "JOIN"));
                break;
            case "T-SQL":
                keywords.addAll(Set.of("SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR", "NOT", "NULL", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "ON", "GROUP", "BY", "ORDER", "HAVING"));
                break;
            // Agregar más casos según sea necesario para otros lenguajes
        }
    }

    public Token getNextToken() {
        while (currentPosition < code.length() && Character.isWhitespace(code.charAt(currentPosition))) {
            currentPosition++;
        }

        if (currentPosition >= code.length()) {
            return new Token(TokenType.EOF, "");
        }

        char currentChar = code.charAt(currentPosition);
        if (Character.isLetter(currentChar)) {
            StringBuilder lexemeBuilder = new StringBuilder();
            while (currentPosition < code.length() && (Character.isLetterOrDigit(code.charAt(currentPosition)) || code.charAt(currentPosition) == '_')) {
                lexemeBuilder.append(code.charAt(currentPosition));
                currentPosition++;
            }
            String lexeme = lexemeBuilder.toString();
            if (keywords.contains(lexeme.toUpperCase())) {
                return new Token(TokenType.KEYWORD, lexeme.toUpperCase());
            } else {
                return new Token(TokenType.IDENTIFIER, lexeme);
            }
        } else if (Character.isDigit(currentChar)) {
            StringBuilder lexemeBuilder = new StringBuilder();
            while (currentPosition < code.length() && (Character.isDigit(code.charAt(currentPosition)) || code.charAt(currentPosition) == '.')) {
                lexemeBuilder.append(code.charAt(currentPosition));
                currentPosition++;
            }
            return new Token(TokenType.NUMBER, lexemeBuilder.toString());
        } else {
            currentPosition++;
            switch (currentChar) {
                case '(':
                    return new Token(TokenType.LEFT_PAREN, "(");
                case ')':
                    return new Token(TokenType.RIGHT_PAREN, ")");
                case ',':
                    return new Token(TokenType.COMMA, ",");
                // Agregar más casos según sea necesario para otros caracteres especiales
                default:
                    return new Token(TokenType.UNKNOWN, String.valueOf(currentChar));
            }
        }
    }
}

enum TokenType {
    KEYWORD,
    IDENTIFIER,
    NUMBER,
    LEFT_PAREN,
    RIGHT_PAREN,
    COMMA,
    // Agregar más tipos de token según sea necesario
    EOF,
    UNKNOWN
}

class Token {
    private TokenType type;
    private String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}';
    }
}
