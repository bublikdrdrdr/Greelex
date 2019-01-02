package app.parser;

import app.exceptions.DataFormatException;
import app.exceptions.SymbolNotFoundException;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EntityHeaderParser extends FileEntityParser<Map<EntityHeaderParser.Keyword, String>> {

    private static final Pattern CHECKING_PATTERN = Pattern.compile("^[A-Za-z_$][\\w$]*(\\([A-Za-z_$][\\w$]*\\))?(\\s*:?\\s*\"\\w+\")?\\s*$");

    protected EntityHeaderParser(char[] content, TokenBounds tokenBounds) {
        super(content, tokenBounds);
    }

    @Override
    public Map<Keyword, String> parse() {
        if (!checkSyntax())
            throw new DataFormatException("Bad entity header format", getTokenBounds().getStart());

        Map<Keyword, String> resultMap = new EnumMap<>(Keyword.class);
        int startIndex = getTokenBounds().getStart();
        startIndex = findSuperclassAndReturnIndex(resultMap, startIndex);
        findSqlName(resultMap, startIndex);
        if (!resultMap.containsKey(Keyword.CLASS)) {
            resultMap.put(Keyword.CLASS, fromBounds(getTokenBounds()));
        }
        return resultMap;
    }

    private boolean checkSyntax() {
        return CHECKING_PATTERN.matcher(new String(
                Arrays.copyOfRange(getContent(), getTokenBounds().getStart(), getTokenBounds().getEnd())))
                .matches();
    }

    private int findSuperclassAndReturnIndex(Map<Keyword, String> resultMap, int startIndex) {
        try {
            int nextSuperclassKeyword = getNextIndex(startIndex, false, '(');
            int endSuperclassKeyword = getNextIndex(nextSuperclassKeyword, false, ')');
            resultMap.put(Keyword.CLASS, fromBounds(startIndex, nextSuperclassKeyword));
            resultMap.put(Keyword.SUPER, fromBounds(nextSuperclassKeyword + 1, endSuperclassKeyword));
            return endSuperclassKeyword + 1;
        } catch (SymbolNotFoundException ignored) {
            return startIndex;
        }
    }

    private void findSqlName(Map<Keyword, String> resultMap, int startIndex) {
        try {
            int colonSymbol = getNextIndex(startIndex, false, ':');
            int openQuotesSymbol = getNextIndex(startIndex, false, '"');
            int closeQuotesSymbol = getNextIndex(openQuotesSymbol + 1, false, '"');
            if (!resultMap.containsKey(Keyword.CLASS)) {
                resultMap.put(Keyword.CLASS, fromBounds(getTokenBounds().getStart(), colonSymbol));
            }
            resultMap.put(Keyword.SQL, fromBounds(openQuotesSymbol + 1, closeQuotesSymbol));
        } catch (SymbolNotFoundException ignored) {
        }
    }

    public enum Keyword {CLASS, SQL, SUPER}
}
