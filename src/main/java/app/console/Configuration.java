package app.console;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Configuration {

    private final boolean stacktrace;
    private final String mappingFilename;
    private final String packageName;
    private final String path;
}
