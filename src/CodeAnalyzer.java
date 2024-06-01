import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer implements ActionListener {
    private JTextField codeInput;
    private JTextArea resultOutput;
    private Map<String, Set<String>> reservedWords;

    public CodeAnalyzer() {
        JFrame frame = new JFrame("Identificador de Lenguajes de Programación");
        codeInput = new JTextField(50);
        resultOutput = new JTextArea(20, 70);
        resultOutput.setEditable(false);

        JButton analyzeButton = new JButton("Analizar Código");
        analyzeButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ingrese línea de código:"));
        panel.add(codeInput);
        panel.add(analyzeButton);
        panel.add(new JScrollPane(resultOutput));

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        reservedWords = new HashMap<>();
        initializeReservedWords();
    }

    private void initializeReservedWords() {
        reservedWords.put("PL/SQL", Set.of("CREATE", "TABLE", "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR", "NOT", "NULL", "JOIN"));
        reservedWords.put("T-SQL", Set.of("SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR", "NOT", "NULL", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "ON", "GROUP", "BY", "ORDER", "HAVING"));
        reservedWords.put("C++", Set.of("include", "int", "float", "double", "char", "void", "if", "else", "for", "while", "do", "switch", "case", "default", "break", "continue", "return", "class", "public", "private", "protected", "new", "delete"));
        reservedWords.put("Pascal", Set.of("program", "begin", "end", "var", "integer", "real", "boolean", "char", "string", "if", "then", "else", "for", "to", "do", "while", "repeat", "until", "case", "of", "function", "procedure", "array", "record"));
        reservedWords.put("JavaScript", Set.of("function", "var", "let", "const", "if", "else", "switch", "case", "default", "for", "while", "do", "break", "continue", "return", "class", "new", "this", "super", "import", "export"));
        reservedWords.put("HTML", Set.of("html", "head", "title", "body", "div", "span", "h1", "h2", "h3", "h4", "h5", "h6", "p", "a", "img", "ul", "ol", "li", "table", "tr", "td", "th", "form", "input", "button"));
        reservedWords.put("Python", Set.of("def", "class", "if", "elif", "else", "while", "for", "break", "continue", "return", "import", "from", "as", "try", "except", "finally", "with", "lambda", "yield", "global", "nonlocal"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String code = codeInput.getText();
        String result = analyzeCode(code);
        resultOutput.setText(result);
    }

    private String analyzeCode(String code) {
        String language = identifyLanguage(code);
        if (language == null) {
            return "Lenguaje no identificado.";
        }

        StringBuilder result = new StringBuilder();
        result.append("Lenguaje identificado: ").append(language).append("\n");

        result.append("Palabras reservadas: ").append(findReservedWords(code, language)).append("\n");
        result.append("Expresiones lógicas: ").append(findLogicalExpressions(code)).append("\n");
        result.append("Expresiones matemáticas: ").append(findAllMathematicalExpressions(code, language)).append("\n");
        result.append("Variables: ").append(findVariables(code, language)).append("\n");
        result.append("Constantes: ").append(findConstants(code, language)).append("\n");
        result.append("Funciones: ").append(findFunctions(code, language)).append("\n");
        result.append("Clases: ").append(findClasses(code, language)).append("\n");
        result.append("Ciclos: ").append(findLoops(code, language)).append("\n");
        result.append("Condicionales: ").append(findConditionals(code, language)).append("\n");
        result.append("CRUD: ").append(findCRUDOperations(code, language)).append("\n");

        String lexicalErrors = performLexicalAnalysis(code, language);
        String syntaxErrors = performSyntaxAnalysis(code, language);
        String semanticErrors = performSemanticAnalysis(code, language);

        if (lexicalErrors.isEmpty() && syntaxErrors.isEmpty() && semanticErrors.isEmpty()) {
            result.append("Código válido.\nSimulación: ").append(simulateExecution(code, language));
        } else {
            result.append("Errores:\n").append(lexicalErrors).append(syntaxErrors).append(semanticErrors);
        }

        return result.toString();
    }

    private String identifyLanguage(String code) {
        // Simplificación de la identificación de lenguaje con expresiones regulares
        if (code.matches(".*\\bCREATE\\s+TABLE\\b.*")) {
            return "PL/SQL";
        } else if (code.matches(".*\\bSELECT\\b.*") || code.matches(".*\\bINSERT\\b.*") || code.matches(".*\\bUPDATE\\b.*") || code.matches(".*\\bDELETE\\b.*")) {
            return "T-SQL";
        } else if (code.matches(".*#include.*") || code.matches(".*\\bcout\\b.*") || code.matches(".*\\bcin\\b.*")) {
            return "C++";
        } else if (code.matches(".*\\bprogram\\b.*") || code.matches(".*\\bbegin\\b.*") || code.matches(".*\\bend\\b.*")) {
            return "Pascal";
        } else if (code.matches(".*\\bfunction\\b.*") || code.matches(".*\\bdebugger\\b.*") || code.matches(".*\\bextends\\b.*")) {
            return "JavaScript";
        } else if (code.matches(".*<html>.*") || code.matches(".*<body>.*") || code.matches(".*<script>.*")) {
            return "HTML";
        } else if (code.matches(".*\\bdef\\b.*") || code.matches(".*\\breturn\\b.*") || code.matches(".*\\bimport\\b.*")) {
            return "Python";
        }
        return null;
    }

    private String findReservedWords(String code, String language) {
        Set<String> keywords = reservedWords.get(language);
        StringBuilder foundKeywords = new StringBuilder();
        for (String keyword : keywords) {
            if (code.contains(keyword)) {
                foundKeywords.append(keyword).append(" ");
            }
        }
        return foundKeywords.toString().trim();
    }

    private List<String> findLogicalExpressions(String code) {
        List<String> logicalExpressions = new ArrayList<>();
        String regex = "(&&)|(\\|\\|)|(==)|(!=)|(<)|(>)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            logicalExpressions.add(matcher.group());
        }

        return logicalExpressions;
    }

    private List<String> findAllMathematicalExpressions(String code, String language) {
        List<String> variables = Arrays.asList(findVariables(code, language).split(",\\s"));
        String variablePattern = String.join("|", variables);

        Pattern pattern = Pattern.compile("((?<=\\s)|(?<=^))((" + variablePattern + ")|(-?\\d+\\.?\\d*))(?:\\s*[-+*/()]\\s*((" + variablePattern + ")|(-?\\d+\\.?\\d*)))*(?=(\\s|$))");
        Matcher matcher = pattern.matcher(code);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    private String findVariables(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "PL/SQL":
            case "T-SQL":
                pattern = Pattern.compile("\\bdeclare\\s+@\\w+");
                break;
            case "C++":
                pattern = Pattern.compile("\\b(auto|int|float|double|bool|char|wchar_t)\\b\\s+\\w+;");
                break;
            case "Pascal":
                pattern = Pattern.compile("\\b(var)\\b\\s+\\w+\\s*:\\s*\\w+;");
                break;
            case "JavaScript":
                pattern = Pattern.compile("\\b(var|let|const)\\b\\s+\\w+;");
                break;
            case "Python":
                pattern = Pattern.compile("\\b\\w+\\s*=\\s*.*;");
                break;
            case "Java":
                pattern = Pattern.compile("\\b(public|protected|private|static|final)\\s+\\w+\\s+\\w+;");
                break;
            default:
                return "";
        }
        Matcher matcher = pattern.matcher(code);
        StringBuilder matchedVariables = new StringBuilder();
        while(matcher.find()){
            matchedVariables.append(matcher.group()).append(" ");
        }
        return matchedVariables.toString();
    }

    private String findConstants(String code, String language) {
        // Implementación básica para encontrar constantes
        if (code.matches(".*\\bconst\\b.*") || code.matches(".*\\bfinal\\b.*")) {
            return code;
        }
        return "";
    }

    private String findFunctions(String code, String language) {
        // Implementación mejorada para encontrar funciones
        switch (language) {
            case "PL/SQL":
            case "T-SQL":
                return code.matches("(\\bCREATE\\s+FUNCTION\\b)|(\\bCREATE\\s+PROCEDURE\\b)") ? code : "";
            case "C++":
                return code.matches("\\b((auto|int|float|double|void|string|char)\\s+\\w+\\(.*\\)|\\w+::~\\w+\\(\\))\\s*") ? code : "";
            case "Pascal":
                return code.matches("\\b(function|procedure)\\b\\s+\\w+\\(.*\\)\\s*") ? code : "";
            case "JavaScript":
                return code.matches("\\b(function|async\\s+function)\\b\\s*\\w*\\(.*\\)|\\b\\w+\\s*=\\s*\\(.*\\)\\s*=>") ? code : "";
            case "Python":
                return code.matches("\\bdef\\b\\s+\\w+\\(.*\\):") ? code : "";
            default:
                return "";
        }
    }

    private String findClasses(String code, String language) {
        // Implementación básica para encontrar clases
        if (language.equals("C++") || language.equals("JavaScript") || language.equals("Python")) {
            return code.matches(".*\\bclass\\b.*") ? code : "";
        }
        return "";
    }

    private String findLoops(String code, String language) {
        // Implementación básica para encontrar ciclos
        if (code.contains("for") || code.contains("while") || code.contains("do")) {
            return code;
        }
        return "";
    }

    private String findConditionals(String code, String language) {
        // Implementación básica para encontrar condicionales
        if (code.contains("if") || code.contains("else") || code.contains("switch")) {
            return code;
        }
        return "";
    }

    private String findCRUDOperations(String code, String language) {
        StringBuilder crudCode = new StringBuilder();
        String[] lines = code.split("\n");

        for (String line : lines) {
            if (line.contains("CREATE") || line.contains("READ") || line.contains("UPDATE") || line.contains("DELETE")) {
                crudCode.append(line).append("\n");
            }
        }

        return crudCode.toString();
    }

    private String performLexicalAnalysis(String code, String language) {
        // Análisis léxico básico
        Set<String> keywords = reservedWords.get(language);
        for (String token : code.split("\\s+")) {
            if (!keywords.contains(token) && !token.matches("\\w+")) {
                return "Error léxico: Token no válido encontrado: " + token + "\n";
            }
        }
        return "";
    }

    private String performSyntaxAnalysis(String code, String language) {
        // Análisis sintáctico básico
        switch (language) {
            case "PL/SQL":
                if (!code.matches("CREATE TABLE [a-zA-Z_][a-zA-Z0-9_]* \\(.*\\);")) {
                    return "Error sintáctico: La sintaxis de la instrucción SQL es incorrecta.\n";
                }
                break;
            // Agregar más casos según sea necesario para otros lenguajes
        }
        return "";
    }

    private String performSemanticAnalysis(String code, String language) {
        // Análisis semántico básico
        if (language.equals("PL/SQL") && code.contains("CREATE TABLE") && !code.contains(";")) {
            return "Error semántico: Falta el punto y coma al final de la instrucción SQL.\n";
        }
        return "";
    }

    private String simulateExecution(String code, String language) {
        // Simulación de la ejecución del código
        if (language.equals("PL/SQL") && code.contains("CREATE TABLE")) {
            return "Tabla creada exitosamente.";
        }
        return "Ejecución simulada completada.";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CodeAnalyzer());
    }

    void setVisible(boolean b) {
        // Implementación opcional si se necesita
    }
}