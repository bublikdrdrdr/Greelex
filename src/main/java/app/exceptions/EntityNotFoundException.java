package app.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName) {
        super("Entity '" + entityName + "' not found");
    }
}
