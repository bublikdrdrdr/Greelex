package app.structures.syntax;

import app.structures.Utils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NamingFormat {

    ORIGINAL("origin"),
    LOWERCASE("lower"),
    UPPERCASE("upper"),
    LOWERCASE_UNDERSCORE("underscore", "lower_underscore"),
    UPPERCASE_UNDERSCORE("UNDERSCORE", "upper_underscore");

    public static final NamingFormat DEFAULT = UPPERCASE_UNDERSCORE;
    private final String[] symbols;

    NamingFormat(String... symbols) {
        this.symbols = symbols;
    }

    private static boolean containsSymbol(NamingFormat accessMode, String symbol) {
        return accessMode.matches(symbol);
    }

    public static NamingFormat getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), NamingFormat::containsSymbol, DEFAULT);
    }

    public boolean matches(String symbol) {
        return Arrays.stream(getSymbols()).anyMatch(s -> s.equals(symbol));
    }

}
