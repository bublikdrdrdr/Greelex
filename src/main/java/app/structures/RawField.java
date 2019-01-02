package app.structures;

import app.structures.syntax.FetchType;
import app.structures.syntax.FieldProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class RawField {

    private final String name;
    private final String sqlName;
    private final RawFieldType type;
    private final FetchType fetchType;
    private final Set<FieldProperty> properties;
}
