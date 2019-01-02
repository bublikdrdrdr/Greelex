package app.exceptions;

public class DataFormatException extends ParserException {

    public DataFormatException(String message, int startSymbol) {
        super(message, startSymbol);
    }
}
