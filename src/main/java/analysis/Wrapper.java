package analysis;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Wrapper {

    public static List<ASTNode> getChildrenNodes(List<ASTNode> roots) {
        List<ASTNode> nodes = new ArrayList<>();
        for (ASTNode node : roots) {
            nodes.addAll(getChildrenNodes(node));
        }
        return nodes;
    }

    public static List<ASTNode> getChildrenNodes(ASTNode root) {
        ArrayList<ASTNode> nodes = new ArrayList<>();
        if (root == null) {
            return nodes;
        }
        ArrayDeque<ASTNode> que = new ArrayDeque<>();
        que.add(root);
        while (!que.isEmpty()) {
            ASTNode head = que.pollFirst();
            List<StructuralPropertyDescriptor> children = (List<StructuralPropertyDescriptor>) head.structuralPropertiesForType();
            for (StructuralPropertyDescriptor descriptor : children) {
                Object child = head.getStructuralProperty(descriptor);
                if (child == null) {
                    continue;
                }
                if (child instanceof ASTNode) {
                    nodes.add((ASTNode) child);
                    que.addLast((ASTNode) child);
                }
                if (child instanceof List) {
                    List<ASTNode> newChildren = (List<ASTNode>) child;
                    nodes.addAll(newChildren);
                    for (ASTNode node : newChildren) {
                        que.addLast(node);
                    }
                }
            }
        }
        if (nodes.size() == 0) {
            nodes.add(root);
        }
        return nodes;
    }

    public static boolean isLiteral(ASTNode astNode) {
        if (astNode instanceof StringLiteral || astNode instanceof NumberLiteral
                || astNode instanceof BooleanLiteral || astNode instanceof CharacterLiteral
            /*|| expression instanceof NullLiteral || expression instanceof TypeLiteral*/) {
            return true;
        }
        return false;
    }

    public static ArrayList<Statement> getIfSubStatements(IfStatement target) {
        ArrayList<Statement> results = new ArrayList<>();
        Statement thenStatement = target.getThenStatement();
        Statement elseStatement = target.getElseStatement();
        if (thenStatement != null) {
            if (thenStatement instanceof Block) {
                results.addAll(((Block) thenStatement).statements());
            } else {
                results.add(thenStatement);
            }
        }
        if (elseStatement != null) {
            if (elseStatement instanceof Block) {
                results.addAll((List<Statement>) ((Block) elseStatement).statements());
            } else {
                results.add(elseStatement);
            }
        }
        return results;
    }

    /*
        This function can get all sub-statements of specific statement(s).
        Sub-statement is defined as statement without containing block
     */
    public static List<Statement> getSubStatements(List<Statement> sourceStatements) {
        List<Statement> results = new ArrayList<>();
        ArrayDeque<Statement> que = new ArrayDeque<>();
        que.addAll(sourceStatements);
        while (!que.isEmpty()) {
            Statement head = que.pollFirst();
            if (head instanceof IfStatement) {
                que.addAll(getIfSubStatements((IfStatement) head));
                continue;
            }
            if (head instanceof TryStatement) {
                que.addAll(((TryStatement) head).getBody().statements());
                continue;
            }
            if (LoopStatement.isLoopStatement(head)) {
                LoopStatement loopStatement = new LoopStatement(head);
                Statement body = loopStatement.getBody();
                if (body instanceof Block) {
                    que.addAll((List<Statement>) ((Block) body).statements());
                } else {
                    que.add(body);
                }
                continue;
            }
            results.add(head);
        }
        return results;
    }

    public static List<Statement> getSubStatements(Statement srcStatement) {
        List<Statement> results = new ArrayList<>();
        ArrayDeque<Statement> que = new ArrayDeque<>();
        que.add(srcStatement);
        while (!que.isEmpty()) {
            Statement head = que.pollFirst();
            if (head instanceof IfStatement) {
                que.addAll(getIfSubStatements((IfStatement) head));
                continue;
            }
            if (head instanceof TryStatement) {
                que.addAll(((TryStatement) head).getBody().statements());
                continue;
            }
            if (LoopStatement.isLoopStatement(head)) {
                LoopStatement loopStatement = new LoopStatement(head);
                Statement body = loopStatement.getBody();
                if (body instanceof Block) {
                    que.addAll((List<Statement>) ((Block) body).statements());
                } else {
                    que.add(body);
                }
                continue;
            }
            results.add(head);
        }
        return results;
    }

    public static List<ASTNode> getAllNodes(List<ASTNode> srcNodes) {
        List<ASTNode> resNodes = new ArrayList<>();
        ArrayDeque<ASTNode> que = new ArrayDeque<>();
        que.addAll(srcNodes);
        while (!que.isEmpty()) {
            ASTNode head = que.pollFirst();
            resNodes.add(head);
            if (head instanceof IfStatement) {
                que.addAll(getIfSubStatements((IfStatement) head));
                continue;
            }
            if (head instanceof TryStatement) {
                que.addAll(((TryStatement) head).getBody().statements());
                continue;
            }
            if (LoopStatement.isLoopStatement(head)) {
                LoopStatement loopStatement = new LoopStatement(head);
                Statement body = loopStatement.getBody();
                if (body instanceof Block) {
                    que.addAll((List<Statement>) ((Block) body).statements());
                } else {
                    que.add(body);
                }
                continue;
            }
        }
        return resNodes;
    }

    public static List<Statement> getAllStatements(List<Statement> sourceStatements) {
        List<Statement> results = new ArrayList<>();
        if (sourceStatements == null || sourceStatements.size() == 0) {
            return results;
        }
        ArrayDeque<Statement> que = new ArrayDeque<>();
        que.addAll(sourceStatements);
        while (!que.isEmpty()) {
            Statement head = que.pollFirst();
            results.add(head);
            if (head instanceof IfStatement) {
                que.addAll(getIfSubStatements((IfStatement) head));
                continue;
            }
            if (head instanceof TryStatement) {
                que.addAll(((TryStatement) head).getBody().statements());
                continue;
            }
            if (LoopStatement.isLoopStatement(head)) {
                LoopStatement loopStatement = new LoopStatement(head);
                Statement body = loopStatement.getBody();
                if (body instanceof Block) {
                    que.addAll((List<Statement>) ((Block) body).statements());
                } else {
                    que.add(body);
                }
                continue;
            }
        }
        return results;
    }

    public static String createMethodSignature(MethodDeclaration method) {
        StringBuilder signature = new StringBuilder();
        List<ASTNode> parameters = method.parameters();
        signature.append(method.getName().toString());
        for (ASTNode parameter : parameters) {
            if (parameter instanceof SingleVariableDeclaration) {
                SingleVariableDeclaration svd = (SingleVariableDeclaration) parameter;
                signature.append(":" + svd.getType().toString());
            } else {
                System.err.println("What a Fucked Parameter: " + parameter);
                System.exit(-1);
            }
        }
        return signature.toString();
    }

    public static HashSet<String> getIdentifiers(Block block) {
        HashSet<String> identifiers = new HashSet<>();
        for (Statement statement : (List<Statement>) block.statements()) {
            List<ASTNode> subNodes = getChildrenNodes(statement);
            for (ASTNode subNode : subNodes) {
                if (subNode instanceof SimpleName) {
                    identifiers.add(((SimpleName) subNode).getIdentifier());
                }
            }
        }
        return identifiers;
    }

    public static ASTNode getStatementOfNode(ASTNode node) {
        if (node == null) {
            return null;
        }
        ASTNode parNode = node.getParent();
        while (true) {
            if(parNode == null || parNode.equals(parNode.getParent())) {
                break;
            }
            if(parNode instanceof Statement || parNode instanceof FieldDeclaration) {
                return parNode;
            }
            parNode = parNode.getParent();
        }
        return parNode;
    }

    public static ASTNode getDirectBrotherOfStatement(ASTNode statement) {
        ASTNode parent = statement.getParent();
        while (!(parent instanceof Statement)) {
            parent = parent.getParent();
            if (parent == null || parent.equals(parent.getParent())) {
                System.err.println("Error in Finding Brother Statement!");
                System.exit(-1);
            }
        }
        return parent;
    }

    public static ASTNode getFirstBrotherOfStatement(ASTNode statement) {
        if (!(statement instanceof Statement)) {
            return null;
        }
        ASTNode parent = statement.getParent();
        ASTNode currentStatement = statement;
        while (!(parent instanceof Block)) {
            parent = parent.getParent();
            currentStatement = currentStatement.getParent();
            if (parent == null || parent.equals(parent.getParent())) {
                System.err.println("Error in Finding Brother Statement!");
                System.exit(-1);
            }
        }
        if (!(currentStatement instanceof Statement)) {
            System.err.println("Error: Current Statement cannot be casted to Statement!");
        }
        return currentStatement;
    }

    public static Block getDirectBlockOfStatement(ASTNode statement) {
        if (statement instanceof Statement) {
            ASTNode parent = statement.getParent();
            while (!(parent instanceof Block)) {
                parent = parent.getParent();
                if (parent == null || parent.equals(parent.getParent())) {
                    System.err.println("Error in Finding Direct Block!");
                    System.exit(-1);
                }
            }
            return (Block) parent;
        } else {
            return null;
        }
    }

    public static MethodDeclaration getDirectMethodOfStatement(ASTNode node) {
        if (node == null) {
            return null;
        }
        if (node instanceof MethodDeclaration) {
            return (MethodDeclaration) node;
        }
        ASTNode parent = node.getParent();
        while (!(parent instanceof MethodDeclaration)) {
            parent = parent.getParent();
            if (parent == null || parent.equals(parent.getParent())) {
                return null;
            }
        }
        return (MethodDeclaration) parent;
    }

    public static TypeDeclaration getClassOfASTNode(ASTNode node) {
        if(node instanceof TypeDeclaration) {
            return (TypeDeclaration) node;
        }
        ASTNode parent = node.getParent();
        while (parent != null && !(parent instanceof TypeDeclaration)) {
            parent = parent.getParent();
            if (parent == null || parent.equals(parent.getParent())) {
                System.err.println("Error in Finding Type!");
                System.exit(-1);
            }
        }
        return (TypeDeclaration) parent;
    }

    public static Type checkLiteralType(AST ast, Expression literalExpression) {
        if (literalExpression instanceof NumberLiteral) {
            String token = ((NumberLiteral) literalExpression).getToken();
            if (token.contains(".")) {
                return ast.newPrimitiveType(PrimitiveType.DOUBLE);
            } else {
                if (token.contains("L")) {
                    return ast.newPrimitiveType(PrimitiveType.LONG);
                } else {
                    return ast.newPrimitiveType(PrimitiveType.INT);
                }
            }
        }
        if (literalExpression instanceof StringLiteral) {
            return ast.newSimpleType(ast.newSimpleName("String"));
        }
        if (literalExpression instanceof CharacterLiteral) {
            return ast.newPrimitiveType(PrimitiveType.CHAR);
        }
        if (literalExpression instanceof BooleanLiteral) {
            return ast.newPrimitiveType(PrimitiveType.BOOLEAN);
        }
        return ast.newSimpleType(ast.newSimpleName("Object"));
    }

    public static final ASTMatcher matcher = new ASTMatcher();

    public static boolean compareNode(ASTNode node1, ASTNode node2) {
        if (node1.toString().equals(node2.toString())) {
            return true;
        }
        if (node1 instanceof TypeDeclaration) {
            return matcher.match((TypeDeclaration) node1, node2);
        }
        if (node1 instanceof FieldDeclaration) {
            return matcher.match((FieldDeclaration) node1, node2);
        }
        if (node1 instanceof AnnotationTypeDeclaration) {
            return matcher.match((AnnotationTypeDeclaration) node1, node2);
        }
        if (node1 instanceof MethodDeclaration) {
            return matcher.match((MethodDeclaration) node1, node2);
        }
        if (node1 instanceof IfStatement) {
            return matcher.match((IfStatement) node1, node2);
        }
        if (node1 instanceof SwitchStatement) {
            return matcher.match((SwitchStatement) node1, node2);
        }
        if (node1 instanceof WhileStatement) {
            return matcher.match((WhileStatement) node1, node2);
        }
        if (node1 instanceof DoStatement) {
            return matcher.match((DoStatement) node1, node2);
        }
        if (node1 instanceof ForStatement) {
            return matcher.match((ForStatement) node1, node2);
        }
        if (node1 instanceof TryStatement) {
            return matcher.match((TryStatement) node1, node2);
        }
        if (node1 instanceof EnhancedForStatement) {
            return matcher.match((EnhancedForStatement) node1, node2);
        }
        if (node1 instanceof TypeDeclarationStatement) {
            return matcher.match((TypeDeclarationStatement) node1, node2);
        }
        if (node1 instanceof BreakStatement) {
            return matcher.match((BreakStatement) node1, node2);
        }
        if (node1 instanceof EmptyStatement) {
            return matcher.match((EmptyStatement) node1, node2);
        }
        if (node1 instanceof ThrowStatement) {
            return matcher.match((ThrowStatement) node1, node2);
        }
        if (node1 instanceof SwitchStatement) {
            return matcher.match((SwitchStatement) node1, node2);
        }
        if (node1 instanceof Block) {
            return matcher.match((Block) node1, node2);
        }
        return false;
    }

}
