package app.preprocessor;

import app.exceptions.DuplicateObjectException;
import app.exceptions.EntityNotFoundException;
import app.parser.EntityHeaderParser;
import app.structures.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//caution - hard logic
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityLinker {

    public static List<Entity> convert(List<RawEntity> rawEntities) {
        return new EntityLinker().initializeEntities(rawEntities);
    }

    private List<Entity> initializeEntities(List<RawEntity> rawEntities) {
        Map<String, Entity> superClasses = selectSuperClasses(rawEntities);
        List<Entity> linkedEntities = selectFinalEntities(rawEntities, superClasses);
        Map<String, Entity> resultEntities = concatEntities(superClasses, linkedEntities);
        addLinkedFields(rawEntities, resultEntities);
        return new ArrayList<>(resultEntities.values());
    }

    private void addLinkedFields(List<RawEntity> rawEntities, Map<String, Entity> entities) {
        for (Entity entity : entities.values()) {
            RawEntity relativeEntity = rawEntities.stream()
                    .filter(rawEntity -> entity.getClassName().equals(rawEntity.getHeaderKeywords().get(EntityHeaderParser.Keyword.CLASS)))
                    .findAny().orElseThrow(() -> new EntityNotFoundException(entity.getClassName()));
            entity.getFields().addAll(relativeEntity.getFields().stream().map(rawField -> {
                FieldType fieldType = linkFieldType(rawField.getName(), entity.getClassName(), rawField.getType(), entities);
                return new Field(rawField.getName(), rawField.getSqlName(), fieldType, rawField.getFetchType(), rawField.getProperties());
            }).collect(Collectors.toList()));
        }
    }

    private FieldType linkFieldType(String fieldName, String entityName, RawFieldType rawFieldType, Map<String, Entity> entityMap) {
        if (rawFieldType.isRelation()) {
            Entity entity = entityMap.get(rawFieldType.getEntityName());
            if (entity == null) {
                throw new EntityNotFoundException(String.format("Entity '%s' for field '%s' in '%s' not found",
                        rawFieldType.getEntityName(), fieldName, entityName));
            }
            return new FieldType(entity, rawFieldType.getCollectionType(), rawFieldType.getMappedBy());
        } else {
            if (rawFieldType.isCollection()) {
                return new FieldType(rawFieldType.getDataType(), rawFieldType.getCollectionType());
            } else {
                return new FieldType(rawFieldType.getDataType(), rawFieldType.getSize());
            }
        }
    }

    private Map<String, Entity> concatEntities(Map<String, Entity> superClasses, List<Entity> linkedEntities) {
        Map<String, Entity> resultMap = new HashMap<>(superClasses);
        resultMap.putAll(linkedEntities.stream().collect(Collectors.toMap(Entity::getClassName, entity -> entity)));
        return resultMap;
    }


    private List<Entity> selectFinalEntities(List<RawEntity> rawEntities, Map<String, Entity> superClasses) {
        return rawEntities.stream()
                .filter(((Predicate<RawEntity>) this::isSuperClass).negate())
                .map(rawEntity -> createSubClassEntity(superClasses, rawEntity.getHeaderKeywords()))
                .collect(Collectors.toList());
    }

    private Entity createSubClassEntity(Map<String, Entity> superClasses, Map<EntityHeaderParser.Keyword, String> keywords) {
        String superClass = keywords.get(EntityHeaderParser.Keyword.SUPER);
        if (superClass != null) {
            if (!superClasses.containsKey(superClass))
                throw new EntityNotFoundException(superClass);
            return new Entity(keywords.get(EntityHeaderParser.Keyword.CLASS),
                    keywords.get(EntityHeaderParser.Keyword.SQL),
                    superClasses.get(superClass));
        } else {
            return new Entity(keywords.get(EntityHeaderParser.Keyword.CLASS),
                    keywords.get(EntityHeaderParser.Keyword.SQL));
        }
    }

    private Map<String, Entity> selectSuperClasses(List<RawEntity> rawEntities) {
        return rawEntities.stream()
                .filter(this::isSuperClass)
                .map(rawEntity -> {
                    Map<EntityHeaderParser.Keyword, String> keywords = rawEntity.getHeaderKeywords();
                    return new Entity(keywords.get(EntityHeaderParser.Keyword.CLASS),
                            keywords.get(EntityHeaderParser.Keyword.SQL),
                            true);
                }).collect(Collectors.toMap(Entity::getClassName, entity -> entity, (e, e2) -> {
                    throw new DuplicateObjectException(String.format("Multiple abstract entities with name '%s'", e.getClassName()));
                }));
    }

    private boolean isSuperClass(RawEntity rawEntity) {
        return "super".equals(rawEntity.getHeaderKeywords().get(EntityHeaderParser.Keyword.SUPER));
    }

}
