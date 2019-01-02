package app.generator;

import app.exceptions.SemanticException;
import app.generator.structures.AnnotationParam;
import app.structures.Entity;
import app.structures.Field;
import app.structures.MappingFile;
import app.structures.syntax.AccessMode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassGenerator extends AbstractGenerator<Entity, CompilationUnit> {

    private final MappingFile configuration;
    private final String packageName;

    @Override
    public CompilationUnit generate(Entity entity) {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration resultClass = compilationUnit.addClass(entity.getClassName());
        addAnnotations(compilationUnit, resultClass, entity);
        FieldGenerator fieldGenerator = new FieldGenerator(configuration, compilationUnit, resultClass);
        for (Field field : entity.getFields()) {
            fieldGenerator.generate(field);
        }
        if (configuration.getAccessMode() == AccessMode.POJO) {
            resultClass.getFields().forEach(fieldDeclaration -> {
                fieldDeclaration.createSetter();
                fieldDeclaration.createGetter();
            });
        }
        return compilationUnit;
    }

    private void addAnnotations(CompilationUnit compilationUnit, ClassOrInterfaceDeclaration declaration, Entity entity) {
        if (configuration.getAccessMode() == AccessMode.LOMBOK) {
            addLombokAnnotations(declaration, compilationUnit);
        }
        if (entity.isAbstractClass()) {
            checkAbstractClassOrThrow(entity);
            Annotation.addAnnotation(Annotation.MAPPED_SUPERCLASS, compilationUnit, declaration);
        } else {
            if (entity.getSuperclass() != null) {
                declaration.addExtendedType(entity.getSuperclass().getClassName());
            }
            Annotation.addAnnotation(Annotation.ENTITY, compilationUnit, declaration);
            Annotation.addAnnotation(Annotation.TABLE, compilationUnit, declaration,
                    AnnotationParam.ofString("name", getTableName(entity)));
        }
    }

    private void addLombokAnnotations(ClassOrInterfaceDeclaration declaration, CompilationUnit compilationUnit) {
        Annotation.addAnnotation(Annotation.DATA, compilationUnit, declaration);
        Annotation.addAnnotation(Annotation.NO_ARGS_CONSTRUCTOR, compilationUnit, declaration);
        Annotation.addAnnotation(Annotation.ALL_ARGS_CONSTRUCTOR, compilationUnit, declaration);
    }

    private void checkAbstractClassOrThrow(Entity entity) {
        if (entity.isAbstractClass() && (entity.getSuperclass() != null || entity.getSqlName() != null))
            throw new SemanticException(String.format("Abstract class '%s' has sql name", entity.getClassName()));
    }

    private String getTableName(Entity entity) {
        return SqlNamingConverter.getName(entity.getSqlName(), entity.getClassName(), configuration.getTableNaming());
    }
}
