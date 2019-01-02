package app.parser;

import app.structures.RawEntity;
import app.structures.RawField;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class EntityParser extends FileEntityParser<RawEntity> {

    EntityParser(char[] content, TokenBounds tokenBounds) {
        super(content, tokenBounds);
    }

    @Override
    public RawEntity parse() {
        Pair<TokenBounds, TokenBounds> headerBodyBounds = getHeaderBodyBounds();
        Map<EntityHeaderParser.Keyword, String> keywords = new EntityHeaderParser(getContent(), headerBodyBounds.getLeft()).parse();
        List<RawField> fields = new EntityBodyParser(getContent(), headerBodyBounds.getRight()).parse();
        return new RawEntity(keywords, fields);
    }

    private Pair<TokenBounds, TokenBounds> getHeaderBodyBounds() {
        int index = getTokenBounds().getStart();
        int headerStart = getNextIndex(index, NOT_WHITESPACE);
        int openBraces = getNextIndex(index, false, '{');
        int headerEnd = getPreviousIndex(openBraces - 1, NOT_WHITESPACE) + 1;
        int bodyStart = getNextIndex(openBraces + 1, NOT_WHITESPACE);
        int closeBraces = getPreviousIndex(getTokenBounds().getEnd() - 1, bodyStart, false, '}') + 1;
        int bodyEnd = getPreviousIndex(closeBraces - 1, bodyStart, NOT_WHITESPACE);
        return Pair.of(new TokenBounds(headerStart, headerEnd), new TokenBounds(bodyStart, bodyEnd));
    }
}
