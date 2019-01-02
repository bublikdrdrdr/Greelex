package app.exceptions;

public class MappedByFieldNotFoundException extends RuntimeException {

    public MappedByFieldNotFoundException(String entity, String field, String relationEntity, String mappedByField) {
        super(String.format("Entity '%s' doesn't contain field '%s', used in %s:%s", entity, field, relationEntity, mappedByField));
    }
}
