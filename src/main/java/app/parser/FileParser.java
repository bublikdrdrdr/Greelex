package app.parser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileParser {

    private final char[] content;

    public static List<TokenBounds> getTokens(char[] content) {
        return new FileParser(content).getEntities();
    }

    private List<TokenBounds> getEntities() {
        int index = 0;
        List<TokenBounds> tokenBounds = new ArrayList<>();
        while (!isEof(index)) {
            index = findNextNotWhitespace(index);
            int afterCommentSymbol = checkAndSkipComment(index);
            if (index != afterCommentSymbol) {
                index = afterCommentSymbol;
                continue;
            }
            int comma = findNextComma(index);
            if (index != comma) {
                tokenBounds.add(new TokenBounds(index, comma));
            }
            index = comma + 1;
        }
        return tokenBounds;
    }

    private int findNextNotWhitespace(int index) {
        while (!isEof(index)) {
            if (!Character.isWhitespace(content[index]))
                return index;
            index++;
        }
        return index;
    }

    private int checkAndSkipComment(int index) {
        if (!isEof(index + 1)) {
            if (content[index] != '/' || content[index + 1] != '/') {
                return index;
            } else {
                index += 2;
                while (!isEof(index) && (content[index] != '\n' && content[index] != '\r')) {
                    index++;
                }
                return index;
            }
        }
        return index;
    }

    private int findNextComma(int index) {
        boolean hasBraces = false;
        while (!isEof(index)) {
            if (content[index] == '{') {
                hasBraces = true;
            } else if (content[index] == '}') {
                hasBraces = false;
            } else if (!hasBraces && content[index] == ',') {
                return index;
            }
            index++;
        }
        return index;
    }

    private boolean isEof(int index) {
        return content.length <= index;
    }
}
