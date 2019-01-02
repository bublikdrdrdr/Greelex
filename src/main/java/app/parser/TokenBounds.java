package app.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenBounds {

    private final int start;
    private final int end;

    public int length() {
        return end - start;
    }
}
