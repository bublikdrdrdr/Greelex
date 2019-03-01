package app.generator;

import app.generator.structures.AnnotationParam;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public enum Annotation {

    DATA("Data", Constants.LOMBOK_PACKAGE),
    NO_ARGS_CONSTRUCTOR("NoArgsConstructor", Constants.LOMBOK_PACKAGE),
    ALL_ARGS_CONSTRUCTOR("AllArgsConstructor", Constants.LOMBOK_PACKAGE),
    MAPPED_SUPERCLASS("MappedSuperclass", Constants.PERSISTENCE),
    ENTITY("Entity", Constants.PERSISTENCE),
    TABLE("Table", Constants.PERSISTENCE),
    GENERATED_VALUE("GeneratedValue", Constants.PERSISTENCE),
    ID("Id", Constants.PERSISTENCE),
    COLUMN("Column", Constants.PERSISTENCE),
    ONE_TO_ONE("OneToOne", Constants.PERSISTENCE),
    ONE_TO_MANY("OneToMany", Constants.PERSISTENCE),
    MANY_TO_ONE("ManyToOne", Constants.PERSISTENCE),
    JOIN_COLUMN("JoinColumn", Constants.PERSISTENCE),
    GENERATION_TYPE("GenerationType", Constants.PERSISTENCE),
    FETCH_TYPE("FetchType", Constants.PERSISTENCE);

    @Getter
    private final String className;
    private final String importPackage;

    public static void addAnnotation(Annotation annotation, CompilationUnit importsContainer,
                                     BodyDeclaration declaration, Collection<AnnotationParam> annotationParams) {
        declaration.addAnnotation(new NormalAnnotationExpr(new Name(annotation.getClassName()),
                new NodeList<>(annotationParams.stream().filter(Objects::nonNull)
                        .map(AnnotationParam::getParam).collect(Collectors.toList()))));
        importsContainer.addImport(annotation.getImport());
    }

    public static void addAnnotation(Annotation annotation, CompilationUnit importsContainer,
                                     BodyDeclaration declaration, AnnotationParam... annotationParams) {
        addAnnotation(annotation, importsContainer, declaration, Arrays.asList(annotationParams));
    }

    public String getImport() {
        return importPackage + "." + className;
    }

    private static class Constants {
        private static final String LOMBOK_PACKAGE = "lombok";
        private static final String PERSISTENCE = "javax.persistence";
    }
}
