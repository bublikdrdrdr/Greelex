package app.structures.syntax;

import app.structures.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectionType {

    ARRAY("array", "%s[]"),
    COLLECTION("collection", "Collection<%s>", "java.util.Collection"),
    LIST("list", "List<%s>", "java.util.List"),
    SET("set", "Set<%s>", "java.util.Set"),
    ARRAY_LIST("arrayList", "ArrayList<%s>", "java.util.ArrayList"),
    LINKED_LIST("linkedList", "LinkedList<%s>", "java.util.LinkedList"),
    HASH_SET("hashSet", "HashSet<%s>", "java.util.HashSet"),
    TREE_SET("treeSet", "TreeSet<%s>", "java.util.TreeSet");

    public static final CollectionType DEFAULT = LIST;
    private final String symbol;
    private final String javaSymbolPattern;
    private final String requiredImport;

    CollectionType(String symbol, String javaSymbol) {
        this(symbol, javaSymbol, null);
    }

    public static CollectionType getBySymbol(String symbol) {
        return Utils.getObjectBySymbol(symbol, values(), CollectionType::getSymbol, DEFAULT);
    }

    public String compileType(String dataType) {
        return String.format(javaSymbolPattern, dataType);
    }
}
