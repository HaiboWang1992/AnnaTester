package analysis;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.io.FileUtils;
import util.Utility;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jface.text.Document;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static analysis.TypeWrapper.compilerOptions;

public class AnnotationWrapper extends Wrapper {

    private String annotationName;
    private String filePath;
    private String fileName;
    private String packageName;

    private int retention;
    private Set<ElementType> targets;
    private AnnotationTypeDeclaration annotation;

    public AnnotationWrapper(JavaClass clazz, String classPath) {
        this.annotationName = clazz.getClassName().substring(clazz.getClassName().lastIndexOf(".") + 1);
        this.filePath = classPath;
        this.fileName = clazz.getFileName(); // not include postfix
        if ("".equals(clazz.getPackageName())) {
            this.packageName = null;
        } else {
            this.packageName = clazz.getPackageName();
        }
        this.retention = -1;
        this.targets = new HashSet<>();
        for (AnnotationEntry entry : clazz.getAnnotationEntries()) {
            if (entry.toString().contains("Target") || entry.toString().contains("TypeQualifierDefault")) {
                for(ElementValuePair valuePair : entry.getElementValuePairs()) {
                    for (ElementType elementType : ElementType.values()) {
                        String tmp = valuePair.getValue().toString();
                        if(tmp.charAt(0) == '{') {
                            tmp = tmp.substring(1);
                        }
                        if(tmp.endsWith("}")) {
                            tmp = tmp.substring(0, tmp.length() - 1);
                        }
                        String[] tokens = tmp.split(",");
                        for(String token : tokens) {
                            if (("ElementType." + elementType).equals(token) || elementType.toString().equals(token)) {
                                this.targets.add(elementType);
                            }
                        }
                    }
                }
            }
            if (entry.toString().contains("Retention")) {
                for(ElementValuePair valuePair : entry.getElementValuePairs()) {
                    ElementValue value = valuePair.getValue();
                    if (value.toString().contains("SOURCE")) {
                        this.retention = 0;
                    }
                    if (value.toString().contains("CLASS")) {
                        this.retention = 1;
                    }
                    if (value.toString().contains("RUNTIME")) {
                        this.retention = 2;
                    }
                }
            }
        }
    }

    public AnnotationWrapper(String filePath, PackageDeclaration pd, AnnotationTypeDeclaration annotation) {
        this.annotationName = annotation.getName().getFullyQualifiedName();
        this.filePath = filePath;
        this.fileName = Utility.Path2Last(filePath); // not include postfix
        if (pd != null) {
            if (this.fileName.equals(this.annotationName)) {
                this.packageName = pd.getName().getFullyQualifiedName();
            } else { // inner class
                this.packageName = pd.getName().getFullyQualifiedName() + "." + this.fileName;
            }
        } else {
            this.packageName = null;
        }
        this.annotation = annotation;
        this.retention = -1;
        this.targets = new HashSet<>();
        for (ASTNode node : (List<ASTNode>) this.annotation.modifiers()) {
            if (node instanceof NormalAnnotation && ((NormalAnnotation) node).getTypeName().getFullyQualifiedName().equals("Target")) {
                NormalAnnotation normalAnnotation = (NormalAnnotation) node;
                for (ElementType elementType : ElementType.values()) {
                    if (normalAnnotation.toString().contains("ElementType." + elementType.toString())) {
                        this.targets.add(elementType);
                    }
                }
            }
            if (node instanceof SingleMemberAnnotation) {
                SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation) node;
                Expression expression = singleMemberAnnotation.getValue();
                String modifierName = singleMemberAnnotation.getTypeName().getFullyQualifiedName();
                String value = singleMemberAnnotation.getValue().toString().toUpperCase();
                if ("Retention".equals(modifierName)) {
                    if (value.contains("SOURCE")) {
                        this.retention = 0;
                    }
                    if (value.contains("CLASS")) {
                        this.retention = 1;
                    }
                    if (value.contains("RUNTIME")) {
                        this.retention = 2;
                    }
                }
                if (expression instanceof QualifiedName) {
                    String name = ((QualifiedName) expression).getName().getFullyQualifiedName();
                    if ("Target".equals(modifierName)) {
                        for (ElementType elementType : ElementType.values()) {
                            if (elementType.toString().equals(name)) {
                                this.targets.add(elementType);
                            }
                        }
                    }
                }
                if (expression instanceof ArrayInitializer) {
                    List<Expression> expressions = ((ArrayInitializer) expression).expressions();
                    if (expressions.size() > 0) {
                        if (expressions.get(0) instanceof QualifiedName) {
                            for (QualifiedName subExpression : (List<QualifiedName>) ((ArrayInitializer) expression).expressions()) {
                                String name = subExpression.getName().getFullyQualifiedName();
                                for (ElementType elementType : ElementType.values()) {
                                    if (elementType.toString().equals(name)) {
                                        this.targets.add(elementType);
                                    }
                                }
                            }
                        }
                        if (expressions.get(0) instanceof SimpleName) {
                            for (SimpleName subExpression : (List<SimpleName>) ((ArrayInitializer) expression).expressions()) {
                                String name = subExpression.getFullyQualifiedName();
                                for (ElementType elementType : ElementType.values()) {
                                    if (elementType.toString().equals(name)) {
                                        this.targets.add(elementType);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<AnnotationWrapper> annotationParser(String filePath) {
        List<AnnotationWrapper> wrappers = new ArrayList<>();
        Document document = null;
        File targetFile = new File(filePath);
        try {
            document = new Document(FileUtils.readFileToString(targetFile, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setCompilerOptions(compilerOptions);
        parser.setSource(document.get().toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        cu.recordModifications();
        PackageDeclaration pd = cu.getPackage();
        if(pd.getName().getFullyQualifiedName().contains("android")) {
            return wrappers;
        }
        for (Object type : cu.types()) {
            if (type instanceof AnnotationTypeDeclaration) {
                AnnotationWrapper newWrapper = new AnnotationWrapper(filePath, pd, (AnnotationTypeDeclaration) type);
                wrappers.add(newWrapper);
            }
        }
        return wrappers;
    }

    public void printBasicInfo() {
        System.out.println("Filepath: " + this.filePath + " Name: " + this.annotationName);
        if (this.packageName!= null) {
            System.out.println("Package Declaration: " + this.packageName);
        }
        System.out.println("Annotation Name: " + annotation.getName());
        System.out.print("Target: ");
        for (ElementType e : this.targets) {
            System.out.print(e + ", ");
        }
        System.out.print("\n");
        System.out.println("Lifecycle: " + this.retention);
    }

    public AnnotationTypeDeclaration getAnnotation() {
        return this.annotation;
    }

    public String getName() {
        return this.annotationName;
    }

    public String getFullyQualifiedName() {
        return this.packageName + "." + this.annotationName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public int getRetention() {
        return this.retention;
    }

    public Set<ElementType> getTargets() {
        return this.targets;
    }

    public void addTargets() {
        this.targets.add(ElementType.FIELD);
        this.targets.add(ElementType.METHOD);
        this.targets.add(ElementType.PARAMETER);
    }

    public void resetTargets(Set<ElementType> targets) {
        this.targets.clear();
        this.targets.addAll(targets);
    }

    public String getTarget() {
        StringBuilder builder = new StringBuilder();
        for (ElementType e : this.targets) {
            builder.append(e.toString().toUpperCase() + ", ");
        }
        return builder.toString().substring(0, builder.length() - 2);
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAnnotationName() {
        return this.annotationName;
    }

    public boolean isSourceLevel() {
        return this.retention == 0;
    }

    public boolean isClassLevel() {
        return this.retention == 1;
    }

    public boolean isRuntimeLevel() {
        return this.retention == 2;
    }

    public String toString() {
        return "Annotation Wrapper: " +getFullyQualifiedName();
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs instanceof AnnotationWrapper) {
            if(rhs.toString().equals(this.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
