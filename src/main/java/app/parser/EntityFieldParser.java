package app.parser;

import app.exceptions.DataFormatException;
import app.exceptions.SemanticException;
import app.exceptions.SymbolNotFoundException;
import app.exceptions.UnknownTokenException;
import app.structures.FieldType;
import app.structures.RawField;
import app.structures.RawFieldType;
import app.structures.RawTypeToken;
import app.structures.syntax.CollectionType;
import app.structures.syntax.DataType;
import app.structures.syntax.FetchType;
import app.structures.syntax.FieldProperty;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class EntityFieldParser extends FileEntityParser<RawField> {

    private static final Pattern TYPE_COMPLEX = Pattern.compile("^[\\w$]+((\\([\\w$]*\\))|(\\[[\\w$]*])){1,2}$");

    EntityFieldParser(char[] content, TokenBounds tokenBounds) {
        super(content, tokenBounds);
    }

    @Override
    public RawField parse() {
        int index = getTokenBounds().getStart();
        int colonIndex = getNextIndex(index, false, ':');
        List<TokenBounds> propertiesBounds = splitElements(colonIndex + 1);
        String name = fromBounds(trimWhitespace(index, colonIndex));
        try {
            String sqlName = null;

            Iterator<TokenBounds> iterator = propertiesBounds.iterator();
            TokenBounds currentBounds = iterator.next();

            if (isSqlName(currentBounds)) {
                sqlName = fromBounds(currentBounds);
                currentBounds = iterator.next();
            }
            RawFieldType type = parseFieldType(currentBounds);
            AtomicReference<FetchType> fetchTypeReference = new AtomicReference<>();

            Set<FieldProperty> properties = new HashSet<>();

            while (iterator.hasNext()) {
                TokenBounds iteratorBounds = iterator.next();
                putProperty(iteratorBounds, fetchTypeProperty -> {
                    if (fetchTypeReference.get() != null)
                        throw new SemanticException("Multiple fetch types in single field", iteratorBounds.getStart());
                    fetchTypeReference.set(fetchTypeProperty);
                }, properties::add);

            }
            return new RawField(name, sqlName, type, fetchTypeReference.get(), properties);
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            throw new DataFormatException(String.format("Field '%s' doesn't have enough parameters", name), index);
        } catch (SymbolNotFoundException e) {
            String message = e.getSymbol() == null ?
                    String.format("Unknown symbol in field '%s': %s", name, e.getMessage())
                    : String.format("Unknown symbol '%s' in field '%s'", e.getSymbol(), name);
            throw new UnknownTokenException(message, index);
        }
    }

    private void putProperty(TokenBounds tokenBounds, Consumer<FetchType> fetchTypeConsumer, Consumer<FieldProperty> fieldPropertyConsumer) {
        String currentToken = fromBounds(tokenBounds);
        try {
            fetchTypeConsumer.accept(FetchType.getBySymbol(currentToken));
        } catch (SymbolNotFoundException ignored) {
            fieldPropertyConsumer.accept(FieldProperty.getBySymbol(currentToken));
        }
    }

    private List<TokenBounds> splitElements(int fromIndex) {
        List<TokenBounds> resultList = new ArrayList<>();
        Integer tokenStart = NOT_WHITESPACE.test(getContent()[fromIndex]) ? fromIndex : null;
        for (int i = fromIndex; i < getTokenBounds().getEnd(); i++) {
            if (Character.isWhitespace(getContent()[i])) {
                if (tokenStart != null) {
                    resultList.add(new TokenBounds(tokenStart, i));
                    tokenStart = null;
                }
            } else {
                if (tokenStart == null) {
                    tokenStart = i;
                }
            }
        }
        if (tokenStart != null) {
            resultList.add(new TokenBounds(tokenStart, getTokenBounds().getEnd()));
        }
        return resultList;
    }

    private TokenBounds trimWhitespace(int start, int end) {
        return new TokenBounds(getNextIndex(start, NOT_WHITESPACE), getPreviousIndex(end - 1, NOT_WHITESPACE) + 1);
    }

    private boolean isSqlName(TokenBounds bounds) {
        return bounds.length() > 1 && getContent()[bounds.getStart()] == '"' && getContent()[bounds.getEnd() - 1] == '"';
    }

    private RawFieldType parseFieldType(TokenBounds bounds) {
        String stringType = fromBounds(bounds);
        if (TYPE_COMPLEX.matcher(stringType).matches()) {
            RawTypeToken rawTypeToken = splitToken(stringType);
            if ("".equals(rawTypeToken.getSizeOrMappedBy()))
                throw new SemanticException("Size or 'mappedBy' value can't be empty", bounds.getStart());
            CollectionType collectionType = Optional.ofNullable(rawTypeToken.getCollection()).map(CollectionType::getBySymbol).orElse(null);
            return createFieldTypeWithDataTypeOrEntity(rawTypeToken.getEntity(), collectionType, rawTypeToken.getSizeOrMappedBy());
        } else {
            return createFieldTypeWithDataTypeOrEntity(stringType, null, null);
        }
    }

    private RawTypeToken splitToken(String stringType) {
        int sizeOrMappedByOpenIndex = stringType.indexOf('(');
        int sizeOrMappedByCloseIndex = stringType.indexOf(')');
        int collectionOpenIndex = stringType.indexOf('[');
        int collectionCloseIndex = stringType.indexOf(']');

        int firstSpecial = getMinExisting(sizeOrMappedByOpenIndex, collectionOpenIndex);

        String entity = stringType.substring(0, firstSpecial);
        String sizeOrMappedBy = sizeOrMappedByCloseIndex == -1 ? null :
                stringType.substring(sizeOrMappedByOpenIndex + 1, sizeOrMappedByCloseIndex);

        String collection = collectionOpenIndex == -1 ? null :
                stringType.substring(collectionOpenIndex + 1, collectionCloseIndex);

        return new RawTypeToken(entity, sizeOrMappedBy, collection);
    }

    private int getMinExisting(int... indexes) {
        if (indexes.length == 0) return -1;
        int min = indexes[0];
        for (int i = 1; i < indexes.length; i++) {
            if (indexes[i] != -1 && (min > indexes[i] || min == -1)) {
                min = indexes[i];
            }
        }
        return min;
    }

    private RawFieldType createFieldTypeWithDataTypeOrEntity(String dataTypeOrEntity, CollectionType collectionType, String sizeOrMappedBy) {
        Integer size = null;
        try {
            size = FieldType.parseSize(sizeOrMappedBy);
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        try {
            return new RawFieldType(DataType.getBySymbol(dataTypeOrEntity), null, size, collectionType, null);
        } catch (SymbolNotFoundException ignored) {
            return new RawFieldType(null, dataTypeOrEntity, null, collectionType, sizeOrMappedBy);
        }
    }
}
