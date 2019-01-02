package app.generator.structures;

import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotationParam {

    private final String key;
    private final String value;
    private final boolean stringValue;

    public static AnnotationParam ofString(String key, String value) {
        return new AnnotationParam(key, value, true);
    }

    public static AnnotationParam ofValue(String key, String value) {
        return new AnnotationParam(key, value, false);
    }

    public MemberValuePair getParam() {
        return new MemberValuePair(key, stringValue ? new StringLiteralExpr(value) : new NameExpr(value));
    }
}
