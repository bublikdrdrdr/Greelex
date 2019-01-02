package app.exceptions;

public class SemanticException extends ParserException {

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, int startSymbol) {
        super(message, startSymbol);
    }
}
