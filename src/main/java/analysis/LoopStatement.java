package analysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class LoopStatement {

    private byte tag;
    private Statement loopStatement;
    private Statement body;

    public LoopStatement(ASTNode node) {
        this.tag = -1;
        if(node instanceof ForStatement) {
            this.tag = 0;
            this.loopStatement = (ForStatement) node;
            this.body = ((ForStatement) this.loopStatement).getBody();
        }
        if(node instanceof WhileStatement) {
            this.tag = 1;
            this.loopStatement = (WhileStatement) node;
            this.body = ((WhileStatement) this.loopStatement).getBody();
        }
        if(node instanceof DoStatement) {
            this.tag = 2;
            this.loopStatement = (DoStatement) node;
            this.body = ((DoStatement) this.loopStatement).getBody();
        }
        if(node instanceof EnhancedForStatement) {
            this.tag = 3;
            this.loopStatement = (EnhancedForStatement) node;
            this.body = ((EnhancedForStatement) this.loopStatement).getBody();
        }
        if(tag == -1) {
            System.err.println("This is not a Loop Statement!");
        }
    }

    public Statement getLoopStatement() {
        return this.loopStatement;
    }

    public Statement getBody() {
        return this.body;
    }

    public static boolean isLoopStatement(ASTNode node) {
        if (node instanceof EnhancedForStatement || node instanceof ForStatement
                || node instanceof WhileStatement || node instanceof DoStatement) {
            return true;
        }
        return false;
    }

}

