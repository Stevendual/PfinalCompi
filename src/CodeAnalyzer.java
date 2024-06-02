import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer implements ActionListener {
    private JTextArea codeInput;
    private JTextArea resultOutput;
    private Map<String, Set<String>> reservedWords;
    private Map<String, String> symbolTable;

    public CodeAnalyzer() {
        JFrame frame = new JFrame("IDECODEC - Identificador de Lenguajes de Programación");

        codeInput = new JTextArea(20, 30);
        resultOutput = new JTextArea(20, 30);
        resultOutput.setEditable(false);

        JButton analyzeButton = new JButton("Analizar Código");
        analyzeButton.addActionListener(this);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Ingrese el código:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(codeInput), BorderLayout.CENTER);
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Resultado del análisis:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(resultOutput), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);
        splitPane.setDividerLocation(400);

        frame.add(splitPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        reservedWords = new HashMap<>();
        symbolTable = new HashMap<>();
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

    private String identifyLanguage(String code) {
        if (Pattern.compile(".*\\bCREATE\\s+TABLE\\b.*").matcher(code).find()) {
            return "PL/SQL";
        }
        if (Pattern.compile(".*\\b(SELECT|INSERT|UPDATE|DELETE)\\b.*").matcher(code).find()) {
            return "T-SQL";
        }
        if (Pattern.compile(".*#include\\b.*|.*\\b(cout|cin)\\b.*").matcher(code).find()) {
            return "C++";
        }
        if (Pattern.compile(".*\\b(program|begin|end)\\b.*").matcher(code).find()) {
            return "Pascal";
        }
        if (Pattern.compile(".*\\b(function|debugger|extends)\\b.*").matcher(code).find()) {
            return "JavaScript";
        }
        if (Pattern.compile(".*<(html|body|script)>.*").matcher(code).find()) {
            return "HTML";
        }
        if (Pattern.compile(".*\\b(def|return|import)\\b.*").matcher(code).find()) {
            return "Python";
        }
        return null;
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

        result.append("Tabla de símbolos: ").append(symbolTable).append("\n");

        if (lexicalErrors.isEmpty() && syntaxErrors.isEmpty() && semanticErrors.isEmpty()) {
            result.append("Código válido.\nSimulación: ").append(simulateExecution(code, language));
        } else {
            result.append("Errores:\n").append(lexicalErrors).append(syntaxErrors).append(semanticErrors);
        }

        return result.toString();
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
                pattern = Pattern.compile("\\s*[^\\W]([a-zA-Z_][\\w]*|@\\w*)\\b");
                break;
            case "C++":
            case "Java":
                pattern = Pattern.compile("\\b(class|int|float|double|char|string|void)\\s+([a-zA-Z_][\\w]*)\\b");
                break;
            case "JavaScript":
                pattern = Pattern.compile("\\b(var|let|const)\\s+([a-zA-Z_$][\\w$]*)\\b");
                break;
            case "Python":
                pattern = Pattern.compile("\\b([a-zA-Z_][\\w]*)\\s*=\\s*");
                break;
            default:
                return "El lenguaje " + language + " no está soportado.";
        }

        Matcher matcher = pattern.matcher(code);
        Set<String> variables = new HashSet<>();
        while (matcher.find()) {
            variables.add(matcher.group().trim());
        }
        return String.join(", ", variables);
    }

    private String findConstants(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "Java":
                pattern = Pattern.compile("\\bfinal\\s+\\w+\\s+\\w+"); // final int MY_CONSTANT
                break;
            case "C++":
                pattern = Pattern.compile("\\bconst\\s+\\w+\\s+\\w+"); // const int MY_CONSTANT
                break;
            case "Python":
                pattern = Pattern.compile("\\b\\w+\\s*=\\s*\\d+"); // MY_CONSTANT = 100
                break;
            case "JavaScript":
                pattern = Pattern.compile("\\bconst\\s+\\w+\\b"); // const MY_CONSTANT
                break;
        }
        if (pattern == null) {
            return "No implementado para " + language;
        }

        Matcher matcher = pattern.matcher(code);
        Set<String> constants = new HashSet<>();
        while (matcher.find()) {
            constants.add(matcher.group().trim());
        }
        return String.join(", ", constants);
    }

    private String findFunctions(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "C++":
            case "Java":
                pattern = Pattern.compile("\\b\\w+\\s*\\(.*?\\)\\s*\\{");
                break;
            case "Python":
                pattern = Pattern.compile("\\bdef\\s+\\w+\\s*\\(.*?\\):");
                break;
            case "JavaScript":
                pattern = Pattern.compile("\\bfunction\\s+\\w+\\s*\\(.*?\\)\\s*\\{");
                break;
            default:
                return "El lenguaje " + language + " no está soportado.";
        }

        Matcher matcher = pattern.matcher(code);
        List<String> functions = new ArrayList<>();
        while (matcher.find()) {
            functions.add(matcher.group());
        }
        return String.join(", ", functions);
    }

    private String findClasses(String code, String language) {
        // Implementar lógica para encontrar clases según el lenguaje identificado
        return "No implementado para " + language;
    }

    private String findLoops(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "Java":
            case "C++":
            case "JavaScript":
                pattern = Pattern.compile("\\b(for|while|do)\\b.*\\{");
                break;
            case "Python":
                pattern = Pattern.compile("\\b(for|while)\\b.*:");
                break;
            default:
                return "El lenguaje " + language + " no está soportado.";
        }

        Matcher matcher = pattern.matcher(code);
        List<String> loops = new ArrayList<>();
        while (matcher.find()) {
            loops.add(matcher.group());
        }
        return String.join(", ", loops);
    }

    private String findConditionals(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "Java":
            case "C++":
            case "JavaScript":
                pattern = Pattern.compile("\\b(if|else if|else|switch)\\b.*\\{");
                break;
            case "Python":
                pattern = Pattern.compile("\\b(if|elif|else):");
                break;
            case "PL/SQL":
            case "T-SQL":
                pattern = Pattern.compile("(CASE|WHEN|THEN|ELSE|END)\\b", Pattern.CASE_INSENSITIVE);
                break;
            default:
                return "The language " + language + " is not supported.";
        }

        Matcher matcher = pattern.matcher(code);
        List<String> conditionals = new ArrayList<>();
        while (matcher.find()) {
            conditionals.add(matcher.group());
        }
        return String.join(", ", conditionals);
    }

    private String findCRUDOperations(String code, String language) {
        Pattern pattern = null;
        switch (language) {
            case "Python":
                pattern = Pattern.compile("\\bdef\\b\\s*(create|read|update|delete)(\\w*)\\s*\\(");
                break;
            case "PL/SQL":
            case "T-SQL":
                pattern = Pattern.compile("\\b(INSERT INTO|SELECT|UPDATE|DELETE FROM)\\b");
                break;
            default:
                return "El lenguaje " + language + " no está soportado.";
        }

        Matcher matcher = pattern.matcher(code);
        List<String> crudOperations = new ArrayList<>();
        while (matcher.find()) {
            crudOperations.add(matcher.group());
        }
        return String.join(", ", crudOperations);
    }

    private String performLexicalAnalysis(String code, String language) {
        if (!"C++".equals(language)) {
            return "Este analizador solo admite C++.";
        }

        Pattern pattern = Pattern.compile(
                "\\b(int|char|void|double|float|bool|if|else|for|while|return|include|using|namespace|class|public|private|protected)\\b|" +  // palabras clave
                        "\\b\\d+\\b|" +  // literales numéricos
                        "[+\\-*/=]|" +  // operadores
                        "\\b[_a-zA-Z][_a-zA-Z0-9]*\\b"  // identificadores
        );

        Matcher matcher = pattern.matcher(code);
        List<String> tokens = new ArrayList<>();
        List<String> invalidTokens = new ArrayList<>();
        while (matcher.find()) {
            String token = matcher.group();
            tokens.add(token);

            // Verifica si el identificador está bien formado (variables)
            if (token.matches("\\b[_a-zA-Z][_a-zA-Z0-9]*\\b") && !token.matches("_*[A-Za-z][A-Za-z0-9_]*")) {
                invalidTokens.add(token);
            }
        }

        String response = "Tokens encontrados: " + String.join(", ", tokens);
        if (!invalidTokens.isEmpty()) {
            response += "\nIdentificadores inválidos encontrados: " + String.join(", ", invalidTokens);
        }
        return response;
    }

    private String performSyntaxAnalysis(String code, String language) {


        switch (language) {


               case "C++":


                return performCPPSyntaxAnalysis(code);
            case "Pascal":

                return "El lenguaje " + language + " no tiene un análisis sintáctico implementado.";
        }
        return code;
    }
    private String performCPPSyntaxAnalysis(String code) {
        StringBuilder syntaxErrors = new StringBuilder();

        // Verificar la estructura de los bloques if-else
        int ifCount = countOccurrences(code, "\\bif\\b");
        int elseCount = countOccurrences(code, "\\belse\\b");
        if (ifCount != elseCount) {
            syntaxErrors.append("Error: La cantidad de bloques 'if' y 'else' no coincide.\n");
        }

        // Verificar la estructura de los bucles for
        int forCount = countOccurrences(code, "\\bfor\\b");
        int leftBraceCount = countOccurrences(code, "\\{");
        if (forCount > 0 && forCount != leftBraceCount) {
            syntaxErrors.append("Error: La cantidad de bloques 'for' no coincide con la cantidad de llaves '{'.\n");
        }

        // Verificar la estructura de los bucles while
        int whileCount = countOccurrences(code, "\\bwhile\\b");
        if (whileCount > 0 && whileCount != leftBraceCount) {
            syntaxErrors.append("Error: La cantidad de bloques 'while' no coincide con la cantidad de llaves '{'.\n");
        }

        // Verificar la estructura de los bucles do-while
        int doCount = countOccurrences(code, "\\bdo\\b");
        int rightBraceCount = countOccurrences(code, "\\}");
        if (doCount > 0 && doCount != rightBraceCount) {
            syntaxErrors.append("Error: La cantidad de bloques 'do' no coincide con la cantidad de llaves '}'.\n");
        }

        // Verificar la estructura de las declaraciones de funciones
        Pattern functionPattern = Pattern.compile("\\b\\w+\\s+\\w+\\s*\\(.*?\\)\\s*\\{");
        Matcher functionMatcher = functionPattern.matcher(code);
        while (functionMatcher.find()) {
            String functionDeclaration = functionMatcher.group();
            if (!functionDeclaration.contains("return") && !functionDeclaration.contains("void")) {
                syntaxErrors.append("Error: La función '").append(functionDeclaration).append("' no tiene un retorno.\n");
            }
        }

        return syntaxErrors.toString();
    }

    private int countOccurrences(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }


    private String performSemanticAnalysis(String code, String language) {
        StringBuilder semanticErrors = new StringBuilder();

        // Analizar asignaciones de variables
        Pattern assignmentPattern = Pattern.compile("\\b(int|char|double|float|bool|string)\\s+\\w+\\s*(=\\s*[^;]+;|;)");
        Matcher assignmentMatcher = assignmentPattern.matcher(code);
        while (assignmentMatcher.find()) {
            String assignment = assignmentMatcher.group();
            String[] parts = assignment.split("\\s*=\\s*");
            String variable = parts[0].trim();
            String value = parts[1].trim();

            if (!symbolTable.containsKey(variable)) {
                semanticErrors.append("Error: La variable '").append(variable).append("' no ha sido declarada.\n");
            } else {
                String variableType = symbolTable.get(variable);
                if (!isValidAssignment(variableType, value)) {
                    semanticErrors.append("Error: La variable '").append(variable).append("' no puede ser asignada con el valor '").append(value).append("'.\n");
                }
            }
        }

        return semanticErrors.toString();
    }

    private boolean isValidAssignment(String variableType, String value) {
        // Implementar lógica para verificar la validez de la asignación según el tipo de la variable
        // Aquí puedes agregar reglas específicas para cada tipo de dato (int, float, string, etc.)
        // Por ejemplo, si variableType es "int", verificar si value es un entero válido, etc.
        // Devolver true si la asignación es válida, false de lo contrario.
        return true; // Por ahora, asumimos que todas las asignaciones son válidas
    }



    private String simulateExecution(String code, String language) {
        if (!"C++".equals(language)) {
            return "Esta simulación solo soporta C++.";
        }
        Map<String, String> variables = new HashMap<>();
        String[] lines = code.split("\\r?\\n");
        for (String line : lines) {
            Matcher declarationMatcher = Pattern.compile("\\b(int|char|double|float|bool|string)\\s+(\\w+)\\s*(=\\s*([^;]+))?;").matcher(line);
            Matcher assignmentMatcher = Pattern.compile("\\b(\\w+)\\s*=\\s*([^;]+);").matcher(line);
            if (declarationMatcher.matches()) {
                String varType = declarationMatcher.group(1);
                String varName = declarationMatcher.group(2);
                String varValue = declarationMatcher.group(4);
                variables.put(varName, varValue == null ? "No definida" : varValue);
            } else if (assignmentMatcher.matches()) {
                String varName = assignmentMatcher.group(1);
                String varValue = assignmentMatcher.group(2);
                if (variables.containsKey(varName)) {
                    variables.put(varName, varValue);
                } else {
                    return "Error: variable `" + varName + "` no está declarada.";
                }
            }
        }
        return "Estado final de variables: " + variables.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CodeAnalyzer::new);
    }

    public void setVisible(boolean b) {
    }
}
