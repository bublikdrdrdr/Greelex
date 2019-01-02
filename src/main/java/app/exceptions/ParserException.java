package app.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class ParserException extends RuntimeException {

    private final Integer startSymbol;
    private Integer endSymbol;
    private Integer line;

    public ParserException(String message) {
        super(message);
        startSymbol = null;
    }

    public ParserException(String message, int startSymbol) {
        super(message);
        this.startSymbol = startSymbol;
    }

    public ParserException(String message, Throwable cause, int startSymbol) {
        super(message, cause);
        this.startSymbol = startSymbol;
    }

    public ParserException(Throwable cause, int startSymbol) {
        super(cause);
        this.startSymbol = startSymbol;
    }

    public ParserException(String message, int startSymbol, Integer endSymbol, Integer line) {
        super(message);
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
        this.line = line;
    }

    public ParserException(String message, Throwable cause, int startSymbol, Integer endSymbol, Integer line) {
        super(message, cause);
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
        this.line = line;
    }

    public ParserException(Throwable cause, int startSymbol, Integer endSymbol, Integer line) {
        super(cause);
        this.startSymbol = startSymbol;
        this.endSymbol = endSymbol;
        this.line = line;
    }
}
