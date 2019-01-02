package app.structures.syntax;

import app.structures.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FetchType {

    EAGER("eager"),
    LAZY("lazy");

    public static final FetchType DEFAULT = LAZY;
    private final String symbol;

    public static FetchType getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), FetchType::getSymbol, DEFAULT);
    }
}
