package app.generator;


import app.generator.structures.AnnotationParam;
import app.structures.Field;
import app.structures.MappingFile;
import app.structures.syntax.AccessMode;
import app.structures.syntax.FetchType;
import app.structures.syntax.FieldProperty;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FieldGenerator extends AbstractGenerator<Field, Void> {

    private final MappingFile configuration;
    private final CompilationUnit compilationUnit;
    private final ClassOrInterfaceDeclaration classDeclaration;

    @Override
    public Void generate(Field data) {
        FieldDeclaration fieldDeclaration = classDeclaration.addField(data.getType().compileType(), data.getName());
        data.getType().getImports().forEach(compilationUnit::addImport);
        setFieldPublic(fieldDeclaration, configuration.getAccessMode() == AccessMode.PUBLIC);
        if (data.getProperties().contains(FieldProperty.PRIMARY)) {
            addPrimaryAnnotation(fieldDeclaration);
        }
        String name = SqlNamingConverter.getName(data.getSqlName(), data.getName(), configuration.getTableNaming());

        if (data.getType().isRelation()) {
            addRelationAnnotations(data, fieldDeclaration);
        } else {
            addColumnAnnotation(fieldDeclaration, name, data.getProperties().contains(FieldProperty.NULLABLE),
                    data.getProperties().contains(FieldProperty.UNIQUE), data.getType().getSize());
        }
        return null;
    }

    private void addRelationAnnotations(Field data, FieldDeclaration fieldDeclaration) {
        AnnotationParam fetchTypeParam = AnnotationParam.ofValue("fetch", getFetchType(data));
        String mappedBy = data.getType().getMappedBy();
        AnnotationParam mappedByParam = Optional.ofNullable(mappedBy)
                .map(s -> AnnotationParam.ofString("mappedBy", mappedBy))
                .orElse(null);
        AnnotationParam optionalAnnotationParam = AnnotationParam.ofValue("optional",
                Boolean.toString(data.getProperties().contains(FieldProperty.NULLABLE)));

        if (data.getType().isCollection()) {
            Annotation.addAnnotation(Annotation.ONE_TO_MANY, compilationUnit, fieldDeclaration, fetchTypeParam, mappedByParam);
            checkAndAddJoinAnnotation(data, fieldDeclaration);
        } else {
            if (isMappedByCollection(data)) {
                Annotation.addAnnotation(Annotation.MANY_TO_ONE, compilationUnit, fieldDeclaration, optionalAnnotationParam, fetchTypeParam);
                Annotation.addAnnotation(Annotation.JOIN_COLUMN, compilationUnit, fieldDeclaration);
            } else {
                Annotation.addAnnotation(Annotation.ONE_TO_ONE, compilationUnit, fieldDeclaration, mappedByParam, fetchTypeParam, optionalAnnotationParam);
                checkAndAddJoinAnnotation(data, fieldDeclaration);
            }
        }
    }

    private void checkAndAddJoinAnnotation(Field data, FieldDeclaration fieldDeclaration) {
        if (data.getProperties().contains(FieldProperty.JOIN)) {
            Annotation.addAnnotation(Annotation.JOIN_COLUMN, compilationUnit, fieldDeclaration);
        }
    }

    private String getFetchType(Field data) {
        FetchType fetchType = data.getFetchType();
        if (fetchType == null) {
            fetchType = configuration.getCollectionFetchType();
        }
        compilationUnit.addImport(Annotation.FETCH_TYPE.getImport());
        return Annotation.FETCH_TYPE.getClassName() + "." + fetchType.name();
    }

    private boolean isMappedByCollection(Field data) {
        return data.getType().getEntity()
                .getFields().stream()
                .anyMatch(field -> data.getName().equals(field.getType().getMappedBy())
                        && field.getType().isCollection());
    }

    private void setFieldPublic(FieldDeclaration fieldDeclaration, boolean modifierPublic) {
        fieldDeclaration.setModifier(modifierPublic ? Modifier.PUBLIC : Modifier.PRIVATE, true);
    }

    private void addPrimaryAnnotation(FieldDeclaration declaration) {
        Annotation.addAnnotation(Annotation.ID, compilationUnit, declaration);
        declaration.addSingleMemberAnnotation(Annotation.GENERATED_VALUE.getClassName(), new AssignExpr(new NameExpr("strategy"),
                new NameExpr(Annotation.GENERATION_TYPE.getClassName() + ".AUTO"), AssignExpr.Operator.ASSIGN));
        compilationUnit.addImport(Annotation.GENERATED_VALUE.getImport());
        compilationUnit.addImport(Annotation.GENERATION_TYPE.getImport());
    }

    private void addColumnAnnotation(FieldDeclaration declaration, String name, boolean nullable, boolean unique, Integer length) {
        List<AnnotationParam> annotationParams = new ArrayList<>();
        if (name != null) annotationParams.add(AnnotationParam.ofString("name", name));
        annotationParams.add(AnnotationParam.ofValue("nullable", Boolean.toString(nullable)));
        if (unique) annotationParams.add(AnnotationParam.ofValue("unique", "true"));
        if (length != null) annotationParams.add(AnnotationParam.ofValue("length", length.toString()));
        Annotation.addAnnotation(Annotation.COLUMN, compilationUnit, declaration, annotationParams);
    }
}
