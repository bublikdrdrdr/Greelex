package app.structures.syntax;

import app.structures.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FieldProperty {

    NULLABLE("null"),
    UNIQUE("unique"),
    PRIMARY("primary"),
    JOIN("join");

    private final String symbol;

    public static FieldProperty getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), FieldProperty::getSymbol);
    }

}
