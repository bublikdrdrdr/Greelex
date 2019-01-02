package app.exceptions;

import com.sun.istack.internal.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SymbolNotFoundException extends RuntimeException {

    @Nullable
    private String symbol;

    public SymbolNotFoundException(String message) {
        super(message);
    }

    public static SymbolNotFoundException withMessage(String symbol) {
        return new SymbolNotFoundException(String.format("Data type symbol %s not found", symbol)).setSymbol(symbol);
    }
}
