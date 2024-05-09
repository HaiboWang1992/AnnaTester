package analysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.List;

public class ModifiedNode {

    private ASTNode node;
    private List<ASTNode> modifiers;
    private ChildListPropertyDescriptor descriptor;

    public ModifiedNode(ASTNode node, List<ASTNode> modifiers, ChildListPropertyDescriptor descriptor) {
        this.node = node;
        this.modifiers = modifiers;
        this.descriptor = descriptor;
    }

    public boolean canAnnotationInserted(AnnotationWrapper annotation) {
        String name2insert = annotation.getName();
        for(ASTNode node : modifiers) {
            String name = null;
            if(node instanceof MarkerAnnotation) {
                name = ((MarkerAnnotation) node).getTypeName().getFullyQualifiedName();
            }
            if(node instanceof SingleVariableDeclaration) {
                name = ((SingleVariableDeclaration) node).getName().getFullyQualifiedName();
            }
            if(name != null && name.equals(name2insert)) {
                return false;
            }
        }
        return true;
    }

    public ASTNode getNode() {
        return this.node;
    }

    public List<ASTNode> getModifiers() {
        return this.modifiers;
    }

    public ChildListPropertyDescriptor getDescriptor() {
        return this.descriptor;
    }

}