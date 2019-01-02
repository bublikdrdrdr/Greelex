package app.structures;

import app.parser.EntityHeaderParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RawEntity {

    private Map<EntityHeaderParser.Keyword, String> headerKeywords;
    private List<RawField> fields;
}
