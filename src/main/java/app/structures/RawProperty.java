package app.structures;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawProperty {

    private String property;
    private String value;
    private int index;
}
