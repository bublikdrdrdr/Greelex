package app.structures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RawTypeToken {

    private final String entity;
    private final String sizeOrMappedBy;
    private final String collection;
}
