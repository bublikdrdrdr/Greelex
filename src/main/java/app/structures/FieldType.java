package app.structures;

import app.structures.syntax.CollectionType;
import app.structures.syntax.DataType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldType {

    private DataType dataType;
    private Entity entity;
    private Integer size;
    private CollectionType collectionType;
    private String mappedBy;

    public FieldType(DataType dataType) {
        this(dataType, null, null, null, null);
    }

    public FieldType(DataType dataType, CollectionType collectionType) {
        this(dataType, null, null, collectionType, null);
    }

    public FieldType(DataType dataType, Integer size) {
        this(dataType, null, size, null, null);
    }

    public FieldType(Entity entity) {
        this(null, entity, null, null, null);
    }

    public FieldType(Entity entity, CollectionType collectionType, String mappedBy) {
        this(null, entity, null, collectionType, mappedBy);
    }

    public static int parseSize(String size) {
        try {
            char lastChar = size.charAt(size.length() - 1);
            return getOptionalModifier(lastChar)
                    .map(multiplier -> Integer.valueOf(size.substring(0, size.length() - 1)) * multiplier)
                    .orElseGet(() -> Integer.valueOf(size));
        } catch (Exception e) {
            throw new NumberFormatException(String.format("Can't parse value '%s'", size));
        }
    }

    private static Optional<Integer> getOptionalModifier(char c) {
        try {
            return Optional.of(getMultiplierByChar(c));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static int getMultiplierByChar(char c) {
        switch (c) {
            case 'k':
                return 1024;
            case 'm':
                return 1024 * 1024;
            case 'g':
                return 1024 * 1024 * 1024;
            default:
                throw new IllegalArgumentException("Unknown modifier");
        }
    }

    public boolean isRelation() {
        return entity != null;
    }

    public boolean isCollection() {
        return collectionType != null;
    }

    public String compileType() {
        String javaSymbol = isRelation() ? entity.getClassName() : dataType.getJavaSymbol();
        return isCollection() ? getCollectionType().compileType(javaSymbol) : javaSymbol;
    }

    public List<String> getImports() {
        List<String> imports = new ArrayList<>();
        if (dataType != null && dataType.getRequiredImport() != null) {
            imports.add(dataType.getRequiredImport());
        }
        if (collectionType != null && collectionType.getRequiredImport() != null) {
            imports.add(collectionType.getRequiredImport());
        }
        return imports;
    }
}
