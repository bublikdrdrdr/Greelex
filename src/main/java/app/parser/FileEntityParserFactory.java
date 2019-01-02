package app.parser;

import app.exceptions.UnknownTokenException;

import java.util.regex.Pattern;

public class FileEntityParserFactory {

    private static final Pattern CLASS_FIRST_LETTER = Pattern.compile("[A-Za-z_$]");

    public static FileEntityParser getFileEntityParser(char[] content, TokenBounds bounds) {
        char firstSymbol = content[bounds.getStart()];
        if (firstSymbol == '.') {
            return new PropertyParser(content, bounds);
        } else if (CLASS_FIRST_LETTER.matcher("" + firstSymbol).matches()) {
            return new EntityParser(content, bounds);
        } else {
            throw new UnknownTokenException(String.format("Unknown entity starting with '%s' (%d)", firstSymbol, (int) firstSymbol), bounds.getStart());
        }
    }
}
