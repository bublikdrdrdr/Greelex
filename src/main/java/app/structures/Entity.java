package app.structures;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Entity {

    private final String className;
    private final String sqlName;
    private final Entity superclass;
    private final boolean abstractClass;
    private List<Field> fields = new ArrayList<>();

    public Entity(String className, String sqlName) {
        this(className, sqlName, false);
    }

    public Entity(String className, String sqlName, Entity superclass) {
        this(className, sqlName, superclass, false);
    }

    public Entity(String className, String sqlName, boolean abstractClass) {
        this(className, sqlName, null, abstractClass);
    }

}
