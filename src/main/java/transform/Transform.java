package transform;

import analysis.TypeWrapper;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Transform {

    private static List<Transform> transforms;
    public final static HashMap<String, Transform> name2transform;

    public abstract List<ASTNode> check(TypeWrapper wrapper, ASTNode node);
    public abstract boolean run(ASTNode targetNode, TypeWrapper wrapper, ASTNode broNode, ASTNode srcNode);

    public String getIndex() {
        return this.getClass().getSimpleName();
    }

    static {
        transforms = new ArrayList<>();
        name2transform = new HashMap<>();
        for(Transform transform : transforms) {
            name2transform.put(transform.getIndex(), transform);
        }
    }

    public static List<Transform> getTransforms() {
        return transforms;
    }

}
