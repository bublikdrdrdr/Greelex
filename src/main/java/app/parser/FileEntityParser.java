package app.parser;

import app.exceptions.SymbolNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FileEntityParser<T> {

    protected static final Predicate<Character> NOT_WHITESPACE = character -> !Character.isWhitespace(character);
    private final char[] content;
    private final TokenBounds tokenBounds;

    public abstract T parse();

    protected int getPreviousIndex(int endIndex, Predicate<Character> predicate) {
        return getPreviousIndex(endIndex, tokenBounds.getStart(), predicate);
    }

    protected int getPreviousIndex(int endIndex, int untilStartIndex, Predicate<Character> predicate) {
        while (endIndex >= untilStartIndex) {
            if (predicate.test(content[endIndex])) return endIndex;
            endIndex--;
        }
        throw new SymbolNotFoundException("Next index for predicate not found until bounds end");
    }

    protected int getPreviousIndex(int endIndex, int untilStartIndex, boolean invert, char... checkChars) {
        return getPreviousIndex(endIndex, untilStartIndex, character -> {
            for (char c : checkChars) {
                if (character.equals(c)) return !invert;
            }
            return invert;
        });
    }

    protected int getNextIndex(int startIndex, Predicate<Character> predicate) {
        while (startIndex < tokenBounds.getEnd()) {
            if (predicate.test(content[startIndex])) return startIndex;
            startIndex++;
        }
        throw new SymbolNotFoundException("Next index for predicate not found until bounds end");
    }

    protected int getNextIndex(int startIndex, boolean invert, char... checkChars) {
        return getNextIndex(startIndex, character -> {
            for (char c : checkChars) {
                if (character.equals(c)) return !invert;
            }
            return invert;
        });
    }

    protected List<TokenBounds> splitElements(List<Pair<Predicate<Character>, Predicate<Character>>> boundsPredicate) {
        return splitElements(boundsPredicate, tokenBounds);
    }

    protected List<TokenBounds> splitElements(List<Pair<Predicate<Character>, Predicate<Character>>> boundsPredicate, TokenBounds bounds) {
        int index = bounds.getStart();
        List<TokenBounds> result = new ArrayList<>();
        for (Pair<Predicate<Character>, Predicate<Character>> predicatePair : boundsPredicate) {
            int begin = getNextIndex(index, predicatePair.getLeft());
            int end = getNextIndex(begin + 1, predicatePair.getRight());
            index = end + 1;
            result.add(new TokenBounds(begin, end));
        }
        return result;
    }

    protected String fromBounds(TokenBounds tokenBounds) {
        return fromBounds(tokenBounds.getStart(), tokenBounds.getEnd());
    }

    protected String fromBounds(int start, int end) {
        return new String(Arrays.copyOfRange(getContent(), start, end));
    }

}
