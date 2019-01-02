package app.preprocessor;

import app.parser.*;
import app.structures.MappingFile;
import app.structures.RawEntity;
import app.structures.RawProperty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingFileBuilder {

    public static final MappingFileBuilder INSTANCE = new MappingFileBuilder();

    public MappingFile parse(char[] data) {
        MappingFile resultMappingFile = new MappingFile();
        List<RawEntity> rawEntities = new ArrayList<>();
        filterParsers(createParsers(data), rawEntities::add, resultMappingFile::setProperty);
        resultMappingFile.setEntities(EntityLinker.convert(rawEntities));
        MappedByChecker.checkMappedByFieldsOrThrow(resultMappingFile.getEntities());
        return resultMappingFile;
    }

    private List<FileEntityParser> createParsers(char[] data) {
        return FileParser.getTokens(data).stream()
                .map(bounds -> FileEntityParserFactory.getFileEntityParser(data, bounds))
                .collect(Collectors.toList());
    }

    private void filterParsers(List<FileEntityParser> parsers, Consumer<RawEntity> entityCollector, Consumer<RawProperty> propertyConsumer) {
        for (FileEntityParser fileEntityParser : parsers) {
            if (fileEntityParser instanceof PropertyParser) {
                propertyConsumer.accept(((PropertyParser) fileEntityParser).parse());
            } else if (fileEntityParser instanceof EntityParser) {
                entityCollector.accept(((EntityParser) fileEntityParser).parse());
            } else {
                throw new UnsupportedOperationException("Unknown parser: " + fileEntityParser.getClass().getName());
            }
        }
    }
}
