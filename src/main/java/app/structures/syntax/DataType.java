package app.structures.syntax;

import app.structures.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataType {

    BOOLEAN("bool", "Boolean"),
    BYTE("byte", "Byte"),
    CHAR("char", "Character"),
    SHORT("short", "Short"),
    INT("int", "Integer"),
    LONG("long", "Long"),
    FLOAT("float", "Float"),
    DOUBLE("double", "Double"),
    STRING("string", "String"),
    DATE("date", "Date", "java.util.Date"),
    TIMESTAMP("timestamp", "Timestamp", "java.sql.Timestamp"),
    INSTANT("instant", "Instant", "java.time.Instant"),
    BYTE_ARRAY("bytes", "byte[]"),
    BLOB("blob", "Blob", "java.sql.Blob");

    private final String symbol;
    private final String javaSymbol;
    private final String requiredImport;

    DataType(String symbol, String javaSymbol) {
        this(symbol, javaSymbol, null);
    }

    public static DataType getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), DataType::getSymbol);
    }

}
