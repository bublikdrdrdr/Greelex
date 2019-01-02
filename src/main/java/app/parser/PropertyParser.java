package app.parser;

import app.exceptions.DataFormatException;
import app.exceptions.UnknownTokenException;
import app.structures.RawProperty;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public class PropertyParser extends FileEntityParser<RawProperty> {

    private static final Pattern VALUE_PATTERN = Pattern.compile("^[\\w_\\-.*/]*$");

    PropertyParser(char[] content, TokenBounds tokenBounds) {
        super(content, tokenBounds);
    }

    @Override
    public RawProperty parse() {
        int index = checkSyntaxAndGetStartOrThrow();

        Pair<TokenBounds, TokenBounds> keyValueBounds = getBounds(index);

        String key = getSubstring(keyValueBounds.getKey());
        String value = getSubstring(keyValueBounds.getValue());

        checkFormatOrThrow(key, "key", keyValueBounds.getKey().getStart());
        checkFormatOrThrow(value, "value", keyValueBounds.getValue().getStart());

        return new RawProperty(key, value, index);
    }

    private int checkSyntaxAndGetStartOrThrow() {
        int index = getTokenBounds().getStart();
        if (getContent()[index] != '.')
            throw new UnknownTokenException("Unknown property block starting symbol " + getContent()[index], index);
        return index + 1;
    }

    private Pair<TokenBounds, TokenBounds> getBounds(int startingFrom) {
        int keyStart = getNextIndex(startingFrom, NOT_WHITESPACE);
        int setPropertySymbolIndex = getNextIndex(keyStart + 1, false, '=');
        int keyEnd = getPreviousIndex(setPropertySymbolIndex - 1, NOT_WHITESPACE) + 1;
        int valueStart = getNextIndex(setPropertySymbolIndex + 1, NOT_WHITESPACE);
        int valueEnd = getPreviousIndex(getTokenBounds().getEnd() - 1, valueStart, NOT_WHITESPACE) + 1;
        return Pair.of(new TokenBounds(keyStart, keyEnd), new TokenBounds(valueStart, valueEnd));
    }

    private String getSubstring(TokenBounds bounds) {
        return new String(Arrays.copyOfRange(getContent(), bounds.getStart(), bounds.getEnd()));
    }

    private void checkFormatOrThrow(String value, String name, int index) {
        if (!VALUE_PATTERN.matcher(value).matches()) {
            throw new DataFormatException("Bad property " + name + " format", index);
        }
    }
}
