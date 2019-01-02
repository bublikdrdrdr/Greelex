package app.exceptions;

public class UnknownTokenException extends ParserException {

    public UnknownTokenException(String message, int startSymbol) {
        super(message, startSymbol);
    }
}
