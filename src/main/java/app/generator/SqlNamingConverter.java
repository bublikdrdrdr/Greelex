package app.generator;

import app.structures.syntax.NamingFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SqlNamingConverter {

    public static String getName(String sqlName, String name, NamingFormat namingFormat) {
        if (sqlName != null) {
            return sqlName;
        } else {
            return SqlNamingConverter.convertName(name, namingFormat);
        }
    }

    public static String convertName(String className, NamingFormat namingFormat) {
        switch (namingFormat) {
            case ORIGINAL:
                return className;
            case LOWERCASE:
                return className.toLowerCase();
            case UPPERCASE:
                return className.toUpperCase();
            case LOWERCASE_UNDERSCORE:
                return convertToUnderscore(className, false);
            case UPPERCASE_UNDERSCORE:
                return convertToUnderscore(className, true);
            default:
                throw new UnsupportedOperationException("Unknown naming format: " + namingFormat.name());
        }
    }

    private static String convertToUnderscore(String className, boolean uppercase) {
        Character previousChar = null;
        StringBuilder sb = new StringBuilder();
        for (char c : className.toCharArray()) {
            if (previousChar != null && Character.isLowerCase(previousChar) && Character.isUpperCase(c)) {
                sb.append('_');
            }
            previousChar = c;
            sb.append(uppercase ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
