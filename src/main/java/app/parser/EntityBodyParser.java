package app.parser;

import app.exceptions.SymbolNotFoundException;
import app.structures.RawField;

import java.util.ArrayList;
import java.util.List;

public class EntityBodyParser extends FileEntityParser<List<RawField>> {

    EntityBodyParser(char[] content, TokenBounds tokenBounds) {
        super(content, tokenBounds);
    }

    @Override
    public List<RawField> parse() {
        List<RawField> resultList = new ArrayList<>();
        int index = getTokenBounds().getStart();
        while (index < getTokenBounds().getEnd()) {
            int fieldEnd = getNextTokenEnd(index);
            int croppedFieldStart = getNextIndex(index, NOT_WHITESPACE);
            int croppedFieldEnd = getPreviousIndex(fieldEnd - 1, croppedFieldStart, NOT_WHITESPACE) + 1;
            index = fieldEnd + 1;
            resultList.add(new EntityFieldParser(getContent(), new TokenBounds(croppedFieldStart, croppedFieldEnd)).parse());
        }
        return resultList;
    }

    private int getNextTokenEnd(int startingFrom) {
        try {
            return getNextIndex(startingFrom, false, ',');
        } catch (SymbolNotFoundException bodyEndException) {
            return getTokenBounds().getEnd();
        }
    }
}
