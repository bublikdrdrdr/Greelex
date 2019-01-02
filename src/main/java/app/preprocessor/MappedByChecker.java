package app.preprocessor;

import app.exceptions.MappedByFieldNotFoundException;
import app.structures.Entity;

import java.util.List;

public final class MappedByChecker {

    public static void checkMappedByFieldsOrThrow(List<Entity> entities) {
        //omg..
        entities.forEach(entity -> entity.getFields().stream()
                .filter(field -> field.getType().isRelation() && field.getType().getMappedBy() != null)
                .forEach(field -> field.getType()
                        .getEntity().getFields().stream()
                        .filter(relationField -> relationField.getName().equals(field.getType().getMappedBy()))
                        .findAny()
                        .orElseThrow(() -> new MappedByFieldNotFoundException(entity.getClassName(),
                                field.getName(),
                                field.getType().getEntity().getClassName(),
                                field.getType().getMappedBy()))));
    }
}
