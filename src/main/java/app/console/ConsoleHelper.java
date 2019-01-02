package app.console;

import app.generator.MappingGenerator;
import app.generator.structures.GeneratedFile;
import app.preprocessor.MappingFileBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConsoleHelper {

    private static final String STACKTRACE_KEYWORD = "-stacktrace";

    public Configuration parseConfiguration(String[] args) {
        boolean stacktrace = false;
        List<String> arguments = new ArrayList<>();
        for (String a : args) {
            if (a.equals(STACKTRACE_KEYWORD)) {
                stacktrace = true;
            } else {
                arguments.add(a);
            }
        }
        if (arguments.size() != 3) throw new ConsoleArgumentsNumberException();
        return new Configuration(stacktrace, arguments.get(0), arguments.get(1), arguments.get(2));
    }

    public void process(Configuration configuration) throws IOException {
        String mappingContent = new String(Files.readAllBytes(Paths.get(configuration.getMappingFilename())));
        Path generatedFolder = Files.createDirectories(Paths.get(configuration.getPath()));
        MappingGenerator mappingGenerator = new MappingGenerator(configuration.getPackageName());
        for (GeneratedFile generatedFile : mappingGenerator.generate(MappingFileBuilder.INSTANCE.parse(mappingContent.toCharArray()))) {
            Files.write(Paths.get(generatedFolder.toString(), generatedFile.getName() + ".java"),
                    generatedFile.getContent().getBytes());
        }
    }
}
