package app.structures.syntax;

import app.structures.Utils;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AccessMode {

    LOMBOK("lombok"), POJO("pojo", "java"), PUBLIC("public");

    public static final AccessMode DEFAULT = POJO;
    private final String[] symbols;

    AccessMode(String... symbols) {
        this.symbols = symbols;
    }

    private static boolean containsSymbol(AccessMode accessMode, String symbol) {
        return accessMode.matches(symbol);
    }

    public static AccessMode getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), AccessMode::containsSymbol, DEFAULT);
    }

    public boolean matches(String symbol) {
        return Arrays.stream(getSymbols()).anyMatch(s -> s.equals(symbol));
    }
}
