package app.structures;

import app.exceptions.SymbolNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static <T> T getObjectBySymbol(String symbol, T[] values, BiPredicate<T, String> filter, T defaultValue) {
        if (symbol.isEmpty() && defaultValue != null) return defaultValue;
        return getObjectBySymbol(symbol, values, filter);
    }

    public static <T> T getObjectBySymbol(String symbol, T[] values, BiPredicate<T, String> filter) {
        return Arrays.stream(values)
                .filter(t -> filter.test(t, symbol))
                .findAny()
                .orElseThrow(() -> SymbolNotFoundException.withMessage(symbol));
    }

    public static <T> T getObjectBySymbol(String symbol, T[] values, Function<T, String> symbolFieldSupplier, T defaultValue) {
        if (symbol.isEmpty() && defaultValue != null) return defaultValue;
        return getObjectBySymbol(symbol, values, symbolFieldSupplier);
    }

    public static <T> T getObjectBySymbol(String symbol, T[] values, Function<T, String> symbolFieldSupplier) {
        return Arrays.stream(values)
                .filter(t -> symbolFieldSupplier.apply(t).equals(symbol))
                .findAny()
                .orElseThrow(() -> SymbolNotFoundException.withMessage(symbol));
    }
}
