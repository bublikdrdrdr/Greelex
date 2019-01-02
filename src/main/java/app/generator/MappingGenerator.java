package app.generator;

import app.generator.structures.GeneratedFile;
import app.structures.MappingFile;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MappingGenerator extends AbstractGenerator<MappingFile, List<GeneratedFile>> {

    private final String packageName;

    @Override
    public List<GeneratedFile> generate(MappingFile mappingFile) {
        ClassGenerator classGenerator = new ClassGenerator(mappingFile, packageName);
        return mappingFile.getEntities().stream()
                .map(entity -> new GeneratedFile(entity.getClassName(), classGenerator.generate(entity).toString()))
                .collect(Collectors.toList());
    }
}
