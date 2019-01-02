package app.structures;

import app.structures.syntax.FetchType;
import app.structures.syntax.FieldProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Field {

    private final String name;
    private final String sqlName;
    private final FieldType type;
    private final FetchType fetchType;
    private final Set<FieldProperty> properties;

}
