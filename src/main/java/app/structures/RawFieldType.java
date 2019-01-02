package app.structures;

import app.structures.syntax.CollectionType;
import app.structures.syntax.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawFieldType {

    private DataType dataType;
    private String entityName;
    private Integer size;
    private CollectionType collectionType;
    private String mappedBy;

    public RawFieldType(DataType dataType) {
        this(dataType, null, null, null, null);
    }

    public RawFieldType(DataType dataType, CollectionType collectionType) {
        this(dataType, null, null, collectionType, null);
    }

    public RawFieldType(DataType dataType, int size) {
        this(dataType, null, size, null, null);
    }

    public RawFieldType(String entity) {
        this(null, entity, null, null, null);
    }

    public RawFieldType(String entity, CollectionType collectionType, String mappedBy) {
        this(null, entity, null, collectionType, mappedBy);
    }

    public boolean isRelation() {
        return entityName != null;
    }

    public boolean isCollection() {
        return collectionType != null;
    }
}
