package app.structures;

import app.exceptions.UnknownTokenException;
import app.structures.syntax.AccessMode;
import app.structures.syntax.FetchType;
import app.structures.syntax.NamingFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MappingFile {

    private AccessMode accessMode = AccessMode.DEFAULT;
    private FetchType relationFetchType = FetchType.EAGER;
    private FetchType collectionFetchType = FetchType.LAZY;
    private NamingFormat tableNaming = NamingFormat.DEFAULT;
    private NamingFormat columnNaming = NamingFormat.DEFAULT;
    @Setter
    private List<Entity> entities = new ArrayList<>();

    public void setProperty(RawProperty rawProperty) {
        String propertyValue = rawProperty.getValue();
        switch (rawProperty.getProperty()) {
            case "accessMode":
                accessMode = AccessMode.getBySymbol(propertyValue);
                break;
            case "relationFetchType":
                relationFetchType = FetchType.getBySymbol(propertyValue);
                break;
            case "collectionFetchType":
                collectionFetchType = FetchType.getBySymbol(propertyValue);
                break;
            case "tableNaming":
                tableNaming = NamingFormat.getBySymbol(propertyValue);
                break;
            case "columnNaming":
                columnNaming = NamingFormat.getBySymbol(propertyValue);
                break;
            default:
                throw new UnknownTokenException("Unknown property '" + rawProperty.getProperty() + "'", rawProperty.getIndex());
        }
    }
}
