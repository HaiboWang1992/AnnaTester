package analysis;

import com.sun.jdi.Method;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.junit.jupiter.api.Assertions;
import util.Utility;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static analysis.Schedule.subFolder2index;
import static util.Utility.CLASS_FOLDER_PATH;
import static util.Utility.COMPILE;
import static util.Utility.EVALUATION_PATH;
import static util.Utility.GOOGLE_FORMAT_PATH;
import static util.Utility.INJECTION_TESTING;
import static util.Utility.Path2Last;
import static util.Utility.TEST_CHECKSTYLE;
import static util.Utility.USE_FORMAT;
import static util.Utility.filepath2annotation;
import static util.Utility.hasAllArgsConstructor;
import static util.Utility.hasConstructor;
import static util.Utility.hasNoArgsConstructor;
import static util.Utility.mutant2seed;
import static util.Utility.sep;

public class TypeWrapper extends Wrapper {

    public static Map compilerOptions = JavaCore.getOptions();

    static {
        compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.latestSupportedJavaVersion());
        compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.latestSupportedJavaVersion());
        compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.latestSupportedJavaVersion());
    }

    private static int mutantCounter = 0;

    private AST ast;
    private ASTRewrite astRewrite;
    private CompilationUnit cu;

    private String fileName;
    private String initSeedPath;
    private String classFolderPath;
    private Document document;
    private ASTParser parser;
    private String filePath;
    private String folderPath;
    private String folderName;
    private String parentPath;
    private String mutantFolderPath;
    private List<TypeDeclaration> types;
    private List<ASTNode> priorNodes;
    private List<ASTNode> allNodes;
    private HashMap<String, List<ASTNode>> method2statements;
    private HashMap<String, HashSet<String>> method2identifiers;

    private AnnotationWrapper insertedAnnotationWrapper;

    public TypeWrapper(String filePath) {
        this.filePath = filePath;
        this.initSeedPath = filePath;
        File targetFile = new File(filePath);
        try {
            this.document = new Document(FileUtils.readFileToString(targetFile, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.folderPath = targetFile.getParentFile().getAbsolutePath();
        this.folderName = targetFile.getParentFile().getName();
        this.fileName = targetFile.getName().substring(0, targetFile.getName().length() - 5);
        this.classFolderPath = CLASS_FOLDER_PATH + sep + this.fileName;
        this.parentPath = null;
        this.mutantFolderPath = EVALUATION_PATH + sep + "mutants" + sep + folderName;
        this.parse2nodes();
    }

    public TypeWrapper(String fileName, String filepath, String content, TypeWrapper parentWrapper, AnnotationWrapper insertedAnnotationWrapper) {
        this.filePath = filepath;
        this.initSeedPath = parentWrapper.initSeedPath;
        this.folderName = parentWrapper.folderName;
        this.fileName = fileName;
        this.classFolderPath = CLASS_FOLDER_PATH + sep + this.fileName;
        this.document = new Document(content);
        this.mutantFolderPath = EVALUATION_PATH + sep + "mutants" + sep + folderName;
        this.parentPath = parentWrapper.filePath;
        File targetFile = new File(filePath);
        this.folderPath = targetFile.getParentFile().getAbsolutePath();
        this.insertedAnnotationWrapper = insertedAnnotationWrapper;
        this.parse2nodes();
    }

    private void parse2nodes() {
        String code = this.document.get();
        this.parser = ASTParser.newParser(AST.getJLSLatest());
        this.parser.setSource(code.toCharArray());
        this.parser.setKind(ASTParser.K_COMPILATION_UNIT);
        this.parser.setCompilerOptions(compilerOptions);
        this.cu = (CompilationUnit) parser.createAST(null);
        this.ast = cu.getAST();
        this.astRewrite = ASTRewrite.create(this.ast);
        this.cu.recordModifications();
        this.types = new ArrayList<>();
        List<ASTNode> initTypes = (List<ASTNode>) this.cu.types();
        for (ASTNode type : initTypes) {
            if (type instanceof TypeDeclaration) {
                this.types.add((TypeDeclaration) type);
            }
        }
        this.allNodes = new ArrayList<>();
        this.method2statements = new HashMap<>();
        this.method2identifiers = new HashMap<>();
        int initializerCount = 0;
        for (TypeDeclaration type : this.types) {
            this.allNodes.add(type);
            ArrayDeque<ASTNode> components = new ArrayDeque<>(type.bodyDeclarations());
            while(!components.isEmpty()) {
                ASTNode component = components.pollFirst();
                this.allNodes.add(component);
                if(component instanceof TypeDeclaration) {
                    components.addAll(((TypeDeclaration) component).bodyDeclarations());
                }
                if (component instanceof Initializer) {
                    Block block = ((Initializer) component).getBody();
                    HashSet<String> ids;
                    List<ASTNode> statements;
                    if (block != null || block.statements().size() > 0) {
                        ids = getIdentifiers(block);
                        statements = getAllStatements(block.statements());
                        this.allNodes.addAll(statements);
                    } else {
                        ids = new HashSet<>();
                        statements = new ArrayList<>();
                    }
                    this.method2identifiers.put(type.getName().toString() + ":Initializer" + initializerCount, ids);
                    this.method2statements.put(type.getName().toString() + ":Initializer" + initializerCount++, statements);
                }
                if (component instanceof MethodDeclaration) {
                    HashSet<String> ids;
                    MethodDeclaration method = (MethodDeclaration) component;
                    if(method.parameters().size() > 0) {
                        this.allNodes.addAll(method.parameters());
                    }
                    List<ASTNode> statements;
                    Block block = method.getBody();
                    if (block != null && block.statements().size() > 0) {
                        statements = getAllStatements(block.statements());
                        this.allNodes.addAll(statements);
                        ids = getIdentifiers(((MethodDeclaration) component).getBody());
                    } else {
                        statements = new ArrayList<>();
                        ids = new HashSet<>();
                    }
                    this.method2identifiers.put(type.getName().toString() + ":" + createMethodSignature(method), ids);
                    this.method2statements.put(type.getName().toString() + ":" + createMethodSignature(method), statements);
                }
            }
        }
        List<ASTNode> validNodes = new ArrayList<>();
        if (priorNodes != null && priorNodes.size() > 0) {
            for (ASTNode priorNode : priorNodes) {
                for (ASTNode node : this.allNodes) {
                    if (compareNode(priorNode, node)) {
                        validNodes.add(node);
                    }
                }
            }
        }
        this.priorNodes = new ArrayList<>(validNodes);
    }

    public void updateAST(String source) {
        this.document = new Document(source);
        this.parse2nodes();
    }

    public void rewriteJavaCode() {
        try {
            TextEdit edits = this.astRewrite.rewriteAST(this.document, null);
            edits.apply(this.document);
            String newCode = this.document.get();
            updateAST(newCode);
        } catch (BadLocationException e) {
            System.out.println("Fail to Rewrite Java Document!");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.out.println("OOM init path: " + this.initSeedPath);
            System.out.println("OOM seed path: " + this.filePath);
            e.printStackTrace();
        }

    }

    public int rewriteJavaCode(ASTNode oldNode) {
        int offset = -1;
        TextEdit edits = null;
        try {
            edits = this.astRewrite.rewriteAST(this.document, null);
            edits.apply(this.document);
        } catch (BadLocationException e) {
            System.err.println("Fail to Rewrite Java Document!");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.err.println("OOM: " + this.filePath);
            System.err.println("OOM: " + this.initSeedPath);
            e.printStackTrace();
        }
        TextEdit[] editChildren = edits.getChildren();
        for (TextEdit child : editChildren) {
            if (child instanceof InsertEdit) {
                if (((InsertEdit) child).getText().equals(oldNode.toString())) {
                    offset = child.getOffset();
                }
            }
        }
        String newCode = this.document.get();
        updateAST(newCode);
        return offset;
    }

    public boolean writeToJavaFile() {
        String code = this.getCode();
        try {
            File file = new File(this.filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(this.filePath);
            fileWriter.write(code);
            fileWriter.close();
            if (USE_FORMAT || (TEST_CHECKSTYLE && INJECTION_TESTING)) {
                String[] invokeCommands = new String[5];
                invokeCommands[0] = "java";
                invokeCommands[1] = "-jar";
                invokeCommands[2] = GOOGLE_FORMAT_PATH;
                invokeCommands[3] = "--replace";
                invokeCommands[4] = this.filePath;
                boolean isFormatted = Utility.invokeCommandsByZT(invokeCommands);
                if (!isFormatted) {
                    FileUtils.delete(file);
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Fail to Write to Java File!");
            e.printStackTrace();
        }
        return false;
    }

    private static int nodeIndex = 0;

    public void transformForEqualAnna(List<AnnotationWrapper> annotationWrappers) {
        for (int i = 0; i < this.allNodes.size(); i++) {
            ASTNode candidateNode = this.allNodes.get(i);
            List<AnnotationWrapper> validAnnotations = new ArrayList<>();
            List<ModifiedNode> modifiedNodes = new ArrayList<>();
            for (AnnotationWrapper wrapper : annotationWrappers) {
                ModifiedNode modifiedNode = canASTNodeBeAnnotated(candidateNode, wrapper);
                if (modifiedNode != null) {
                    validAnnotations.add(wrapper);
                    modifiedNodes.add(modifiedNode);
                }
            }
            if (validAnnotations.size() > 1) {
                nodeIndex++;
            } else {
                continue;
            }
            String annotationKey = nodeIndex + "_" + validAnnotations.get(0).getAnnotationName();
            for (int validAnnaIndex = 0; validAnnaIndex < validAnnotations.size(); validAnnaIndex++) {
                AnnotationWrapper annotationWrapper = validAnnotations.get(validAnnaIndex);
                ModifiedNode modifiedNode = modifiedNodes.get(validAnnaIndex);
                boolean canMutate = modifiedNode.canAnnotationInserted(annotationWrapper);
                if (canMutate) {
                    String mutantFileName = "MutantClass" + mutantCounter++;
                    String mutantPath = this.mutantFolderPath + sep + mutantFileName + ".java";
                    HashMap<String, List<String>> annotationIndex2wrappers = subFolder2index.get(Path2Last(this.mutantFolderPath));
                    String content = this.document.get();
                    TypeWrapper mutant = new TypeWrapper(mutantFileName, mutantPath, content, this, annotationWrapper);
                    mutant2seed.put(mutantPath, this.initSeedPath);
                    if (!annotationIndex2wrappers.containsKey(annotationKey)) {
                        annotationIndex2wrappers.put(annotationKey, new ArrayList<>());
                    }
                    annotationIndex2wrappers.get(annotationKey).add(mutant.getFilePath());
                    int oldRowNumber = this.cu.getLineNumber(candidateNode.getStartPosition());
                    int oldColNumber = this.cu.getColumnNumber(candidateNode.getStartPosition());
                    ASTNode newCandidateNode = mutant.searchNodeByPosition(candidateNode, oldRowNumber, oldColNumber);
                    if (newCandidateNode == null) {
                        System.err.println("Old and new TypeWrapper are not matched: " + mutant.getFilePath());
                        System.exit(-1);
                    }
                    ModifiedNode newModifiedNode = canASTNodeBeAnnotated(newCandidateNode, annotationWrapper);
                    ImportDeclaration newImport = mutant.ast.newImportDeclaration();
                    if (annotationWrapper.getPackageName() != null) {
                        newImport.setName(mutant.ast.newQualifiedName(
                                mutant.ast.newName(annotationWrapper.getPackageName()),
                                mutant.ast.newSimpleName(annotationWrapper.getAnnotationName())
                        ));
                    }
                    newImport.setOnDemand(false);
                    List<ImportDeclaration> imports = mutant.cu.imports();
                    boolean isImportExisted = false;
                    for (ImportDeclaration importDeclaration : imports) {
                        if (importDeclaration.getName().getFullyQualifiedName().equals(annotationWrapper.getFullyQualifiedName())) {
                            isImportExisted = true;
                        }
                    }
                    ListRewrite listRewrite;
                    if (!isImportExisted) {
                        listRewrite = mutant.astRewrite.getListRewrite(mutant.cu, CompilationUnit.IMPORTS_PROPERTY);
                        listRewrite.insertLast(newImport, null);
                        Statement placeHolder = (Statement) mutant.astRewrite.createStringPlaceholder("", ASTNode.EMPTY_STATEMENT);
                        listRewrite.insertLast(placeHolder, null);
                    }
                    Annotation newAnnotationNode = wrapAnnotationNode(annotationWrapper, mutant);
                    if(newModifiedNode.getDescriptor() != null) {
                        listRewrite = mutant.astRewrite.getListRewrite(newCandidateNode, newModifiedNode.getDescriptor());
                        listRewrite.insertFirst(newAnnotationNode, null);
                    } else {
                        if (newModifiedNode.getNode() instanceof ExpressionMethodReference) {
                            ExpressionMethodReference srcNode = (ExpressionMethodReference) newModifiedNode.getNode();
                            TypeMethodReference newNode = mutant.getAST().newTypeMethodReference();
                            newNode.setName(mutant.getAST().newSimpleName(srcNode.getName().getFullyQualifiedName()));
                            SimpleName newSimpleName = mutant.getAST().newSimpleName(srcNode.getExpression().toString());
                            SimpleType newSimpleType = mutant.getAST().newSimpleType(newSimpleName);
                            newSimpleType.annotations().add(newAnnotationNode);
                            newNode.setType(newSimpleType);
                            mutant.getAstRewrite().replace(srcNode, newNode, null);
                            mutant.rewriteJavaCode();
                        }
                    }
                    if (COMPILE) {
                        mutant.rewriteJavaCode(newAnnotationNode);
                        if (!mutant.resetClassName()) {
                            continue;
                        }
                        mutant.removePackageDefinition();
                    }
                    mutant.rewriteJavaCode();
                    if (mutant.writeToJavaFile()) {
                        filepath2annotation.put(mutant.getFilePath(), mutant.getInsertedAnnotationWrapper().getFullyQualifiedName());
                    }
                }
            }
        }
    }

    public static HashMap<String, String> variant2add = new HashMap<>();
    public List<TypeWrapper> transformByAnnotationSyntax(AnnotationWrapper annotationWrapper) {
        List<TypeWrapper> variants = new ArrayList<>();
        for (int i = 0; i < this.allNodes.size(); i++) {
            ASTNode candidateNode = this.allNodes.get(i);
            ModifiedNode modifiedNode = canASTNodeBeAnnotated(candidateNode, annotationWrapper);
            if (modifiedNode == null) {
                continue;
            }
            boolean hasMutated = modifiedNode.canAnnotationInserted(annotationWrapper);
            if (hasMutated) {
                String mutantFileName = "MutantClass" + mutantCounter++;
                String mutantPath = mutantFolderPath + sep + mutantFileName + ".java";
                String content = this.document.get();
                TypeWrapper variant = new TypeWrapper(mutantFileName, mutantPath, content, this, annotationWrapper);
                mutant2seed.put(mutantPath, this.initSeedPath);
                int oldRowNumber = this.cu.getLineNumber(candidateNode.getStartPosition());
                int oldColNumber = this.cu.getColumnNumber(candidateNode.getStartPosition());
                ASTNode newCandidateNode = variant.searchNodeByPosition(candidateNode, oldRowNumber, oldColNumber);
                Assertions.assertNotNull(newCandidateNode, "Old and new TypeWrapper are not matched: " + variant.getFilePath());
                if(newCandidateNode instanceof VariableDeclarationStatement) {
                    variant2add.put(variant.getFilePath(), "soot.tagkit.VisibilityLocalVariableAnnotationTag");
                }
                if(newCandidateNode instanceof SingleVariableDeclaration) {
                    variant2add.put(variant.getFilePath(), "soot.tagkit.VisibilityParameterAnnotationTag");
                }
                if(newCandidateNode instanceof FieldDeclaration || newCandidateNode instanceof MethodDeclaration || newCandidateNode instanceof TypeDeclaration) {
                    variant2add.put(variant.getFilePath(), "soot.tagkit.VisibilityAnnotationTag");
                }
                ModifiedNode newModifiedNode = canASTNodeBeAnnotated(newCandidateNode, annotationWrapper);
                if (newModifiedNode == null) {
                    continue;
                }
                List<ImportDeclaration> imports = variant.cu.imports();
                boolean isImportExisted = false;
                for (ImportDeclaration importDeclaration : imports) {
                    if (importDeclaration.getName().getFullyQualifiedName().equals(annotationWrapper.getFullyQualifiedName())) {
                        isImportExisted = true;
                    }
                }
                ListRewrite listRewrite;
                if (!isImportExisted) {
                    ImportDeclaration newImport = variant.ast.newImportDeclaration();
                    if (annotationWrapper.getPackageName() != null) {
                        newImport.setName(variant.ast.newQualifiedName(
                                variant.ast.newName(annotationWrapper.getPackageName()),
                                variant.ast.newSimpleName(annotationWrapper.getAnnotationName())
                        ));
                    }
                    newImport.setOnDemand(false);
                    listRewrite = variant.astRewrite.getListRewrite(variant.cu, CompilationUnit.IMPORTS_PROPERTY);
                    listRewrite.insertLast(newImport, null);
                    Statement placeHolder = (Statement) variant.astRewrite.createStringPlaceholder("", ASTNode.EMPTY_STATEMENT);
                    listRewrite.insertLast(placeHolder, null);
                }
                Annotation newAnnotationNode = variant.getAST().newMarkerAnnotation();
                newAnnotationNode.setTypeName(variant.getAST().newSimpleName(annotationWrapper.getName()));
                if (newModifiedNode.getDescriptor() == null) {
                    if (newModifiedNode.getNode() instanceof ExpressionMethodReference) {
                        ExpressionMethodReference srcNode = (ExpressionMethodReference) newModifiedNode.getNode();
                        TypeMethodReference newNode = variant.getAST().newTypeMethodReference();
                        newNode.setName(variant.getAST().newSimpleName(srcNode.getName().getFullyQualifiedName()));
                        SimpleName newSimpleName = variant.getAST().newSimpleName(srcNode.getExpression().toString());
                        SimpleType newSimpleType = variant.getAST().newSimpleType(newSimpleName);
                        newSimpleType.annotations().add(newAnnotationNode);
                        newNode.setType(newSimpleType);
                        variant.getAstRewrite().replace(srcNode, newNode, null);
                        variant.rewriteJavaCode();
                    }
                } else {
                    listRewrite = variant.astRewrite.getListRewrite(newCandidateNode, newModifiedNode.getDescriptor());
                    listRewrite.insertFirst(newAnnotationNode, null);
                    variant.rewriteJavaCode();
                }
                if (COMPILE) {
                    if (!variant.resetClassName()) {
                        continue;
                    }
                }
                variant.removePackageDefinition();
                variant.rewriteJavaCode();
                if (variant.writeToJavaFile()) {
                    variants.add(variant);
                }
            }
        }
        return variants;
    }

    public List<TypeWrapper> transformByAnnotationInsertion(AnnotationWrapper annotationWrapper) {
        List<TypeWrapper> variants = new ArrayList<>();
        for (int i = 0; i < this.allNodes.size(); i++) {
            ASTNode candidateNode = this.allNodes.get(i);
            ModifiedNode modifiedNode = canASTNodeBeAnnotated(candidateNode, annotationWrapper);
            if (modifiedNode == null) {
                continue;
            }
            boolean hasMutated = modifiedNode.canAnnotationInserted(annotationWrapper);
            if (hasMutated) {
                String mutantFileName = "MutantClass" + mutantCounter++;
                String mutantPath = mutantFolderPath + sep + mutantFileName + ".java";
                String content = this.document.get();
                TypeWrapper mutant = new TypeWrapper(mutantFileName, mutantPath, content, this, annotationWrapper);
                mutant2seed.put(mutantPath, this.initSeedPath);
                int oldRowNumber = this.cu.getLineNumber(candidateNode.getStartPosition());
                int oldColNumber = this.cu.getColumnNumber(candidateNode.getStartPosition());
                ASTNode newCandidateNode = mutant.searchNodeByPosition(candidateNode, oldRowNumber, oldColNumber);
                if (newCandidateNode == null) {
                    System.err.println("Old and new TypeWrapper are not matched: " + mutant.getFilePath());
                    System.exit(-1);
                }
                ModifiedNode newModifiedNode = canASTNodeBeAnnotated(newCandidateNode, annotationWrapper);
                if (newModifiedNode == null) {
                    continue;
                }
                List<ImportDeclaration> imports = mutant.cu.imports();
                boolean isImportExisted = false;
                for (ImportDeclaration importDeclaration : imports) {
                    if (importDeclaration.getName().getFullyQualifiedName().equals(annotationWrapper.getFullyQualifiedName())) {
                        isImportExisted = true;
                    }
                }
                ListRewrite listRewrite;
                if (!isImportExisted) {
                    ImportDeclaration newImport = mutant.ast.newImportDeclaration();
                    if (annotationWrapper.getPackageName() != null) {
                        newImport.setName(mutant.ast.newQualifiedName(
                                mutant.ast.newName(annotationWrapper.getPackageName()),
                                mutant.ast.newSimpleName(annotationWrapper.getAnnotationName())
                        ));
                    }
                    newImport.setOnDemand(false);
                    listRewrite = mutant.astRewrite.getListRewrite(mutant.cu, CompilationUnit.IMPORTS_PROPERTY);
                    listRewrite.insertLast(newImport, null);
                    Statement placeHolder = (Statement) mutant.astRewrite.createStringPlaceholder("", ASTNode.EMPTY_STATEMENT);
                    listRewrite.insertLast(placeHolder, null);
                }
                Annotation newAnnotationNode = mutant.getAST().newMarkerAnnotation();
                newAnnotationNode.setTypeName(mutant.getAST().newSimpleName(annotationWrapper.getName()));
                if (newModifiedNode.getDescriptor() == null) {
                    if (newModifiedNode.getNode() instanceof ExpressionMethodReference) {
                        ExpressionMethodReference srcNode = (ExpressionMethodReference) newModifiedNode.getNode();
                        TypeMethodReference newNode = mutant.getAST().newTypeMethodReference();
                        newNode.setName(mutant.getAST().newSimpleName(srcNode.getName().getFullyQualifiedName()));
                        SimpleName newSimpleName = mutant.getAST().newSimpleName(srcNode.getExpression().toString());
                        SimpleType newSimpleType = mutant.getAST().newSimpleType(newSimpleName);
                        newSimpleType.annotations().add(newAnnotationNode);
                        newNode.setType(newSimpleType);
                        mutant.getAstRewrite().replace(srcNode, newNode, null);
                        mutant.rewriteJavaCode();
                    }
                } else {
                    listRewrite = mutant.astRewrite.getListRewrite(newCandidateNode, newModifiedNode.getDescriptor());
                    listRewrite.insertFirst(newAnnotationNode, null);
                    mutant.rewriteJavaCode();
                }
                if (COMPILE) {
                    if (!mutant.resetClassName()) {
                        continue;
                    }
                }
                mutant.removePackageDefinition();
                mutant.rewriteJavaCode();
                if (mutant.writeToJavaFile()) {
                    variants.add(mutant);
                }
            }
        }
        return variants;
    }

    public List<String> generateNewMutants(TypeWrapper mutant, int annotationOffset, AnnotationWrapper annotation) {
        List<String> newMutantCodes = new ArrayList<>();
        String code = mutant.getCode();
        String[] lines = code.split("\n");
        int annotationLineNumber = mutant.cu.getLineNumber(annotationOffset) - 1;
        String targetLine = lines[annotationLineNumber];
        if (!targetLine.contains(annotation.getName())) {
            if (lines[annotationLineNumber - 1].contains("")) {
                annotationLineNumber--;
            } else {
                if (lines[annotationLineNumber + 1].contains("")) {
                    annotationLineNumber++;
                } else {
                    System.err.println("Target line doesn't contain annotation!");
                    System.exit(-1);
                }
            }
        }
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        prefix.append("\"");
        for (int i = 0; i <= annotationLineNumber; i++) {
            prefix.append(lines[i] + "\n");
        }
        prefix.deleteCharAt(prefix.length() - 1);
        prefix.append("(\"");
        suffix.append("\")\n");
        for (int i = annotationLineNumber + 1; i < lines.length; i++) {
            suffix.append(lines[i] + "\n");
        }
        suffix.append("\"");
        List<String> cmd_list = new ArrayList<>();
        cmd_list.add("python");
        cmd_list.add("run_codex.py");
        cmd_list.add(prefix.toString());
        cmd_list.add(suffix.toString());
        String output = Utility.invokeCommandsByZTWithOutput(cmd_list.toArray(new String[cmd_list.size()]));
        if (output == "") {
            System.err.println("Fail to run the model: " + mutant.filePath);
        }
        JSONObject root = new JSONObject(output);
        JSONArray array = root.getJSONArray("choices");
        Set<String> texts = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject choice = array.getJSONObject(i);
            String code2insert = choice.getString("text");
            if (code2insert == null || code2insert.length() == 0) {
                continue;
            }
            texts.add(code2insert);
        }
        prefix.deleteCharAt(0);
        prefix.deleteCharAt(suffix.length() - 1);
        for (String text : texts) {
            String newCode = prefix + text + suffix;
            newMutantCodes.add(newCode);
        }
        return newMutantCodes;
    }

    public void removePackageDefinition() {
        PackageDeclaration pd = this.cu.getPackage();
        if (pd != null) {
            this.astRewrite.remove(pd, null);
        }
    }

    public static List<String> fail2reset = new ArrayList<>();

    public boolean resetClassName() {
        String srcName = Path2Last(this.parentPath);
        TypeDeclaration clazz = null;
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).getName().getIdentifier().equals(srcName)) {
                clazz = types.get(i);
            }
        }
        if (clazz == null) {
            fail2reset.add(mutant2seed.get(this.filePath));
            return false;
        }
        for (int i = 0; i < clazz.getMethods().length; i++) {
            MethodDeclaration method = clazz.getMethods()[i];
            if (method.getName().getIdentifier().equals(srcName)) {
                this.astRewrite.replace(method.getName(), this.ast.newSimpleName(this.fileName), null);
            }
        }
        this.astRewrite.replace(clazz.getName(), this.ast.newSimpleName(this.fileName), null);
        for (TypeDeclaration td : this.types) {
            List<ASTNode> nodes = getChildrenNodes(td);
            for (int i = 0; i < nodes.size(); i++) {
                ASTNode node = nodes.get(i);
                if (node instanceof SimpleName && ((SimpleName) node).getIdentifier().equals(srcName)) {
                    this.astRewrite.replace(node, this.ast.newSimpleName(this.fileName), null);
                }
            }
        }
        return true;
    }

    public ASTNode searchNodeByPosition(ASTNode oldNode, int oldRowNumber, int oldColNumber) {
        if (oldNode == null) {
            System.err.println("AST Node to be searched is NULL!");
            System.exit(-1);
        }
        for (int i = 0; i < this.allNodes.size(); i++) {
            ASTNode newNode = this.allNodes.get(i);
            int newLineNumber = this.cu.getLineNumber(newNode.getStartPosition());
            int newColNumber = this.cu.getColumnNumber(newNode.getStartPosition());
            if (newLineNumber == oldRowNumber && newColNumber == oldColNumber) {
                if (compareNode(newNode, oldNode)) {
                    return newNode;
                }
            }
        }
        System.err.println("Node is not found!");
        for (int i = 0; i < this.allNodes.size(); i++) {
            ASTNode newNode = this.allNodes.get(i);
            int newLineNumber = this.cu.getLineNumber(newNode.getStartPosition());
            int newColNumber = this.cu.getColumnNumber(newNode.getStartPosition());
            if (newLineNumber == oldRowNumber && newColNumber == oldColNumber) {
                if (compareNode(newNode, oldNode)) {
                    return newNode;
                }
            }
        }
        System.exit(-1);
        return null;
    }

    public void printBasicInfo() {
        PackageDeclaration packageDeclaration = this.cu.getPackage();
        if (packageDeclaration != null) {
            System.out.println("Package Declaration: " + packageDeclaration);
        }
        for (ImportDeclaration importNode : (List<ImportDeclaration>) this.cu.imports()) {
            System.out.println(importNode.getName().getFullyQualifiedName());
        }
        for (TypeDeclaration clazz : this.types) {
            System.out.println("----------Type(Class) Name: " + clazz.getName() + "----------");
            List<ASTNode> super_nodes = clazz.bodyDeclarations();
            for (ASTNode node : super_nodes) {
                System.out.println(node);
            }
            TypeDeclaration[] subTypes = clazz.getTypes();
            for (TypeDeclaration subType : subTypes) {
                System.out.println(subType);
            }
            List<ASTNode> components = clazz.bodyDeclarations();
            for (ASTNode node : components) {
                System.out.println(node);
            }
            FieldDeclaration[] fields = clazz.getFields();
            for (FieldDeclaration field : fields) {
                System.out.println(field);
            }
            MethodDeclaration[] methods = clazz.getMethods();
            for (MethodDeclaration method : methods) {
                List<ASTNode> parentNodes = getChildrenNodes(method);
                System.out.println(parentNodes);
                System.out.println("----------Method Name: " + method.getName() + "----------");
                Block block = method.getBody();
                if (block == null || block.statements().size() == 0) {
                    continue;
                }
                List<Statement> statements = block.statements();
                for (int i = 0; i < statements.size(); i++) {
                    Statement statement = (Statement) block.statements().get(i);
                    System.out.println(statement.toString());
                    List<ASTNode> nodes = getChildrenNodes(statement);
                    for (ASTNode node : nodes) {
                        System.out.println(node + "  " + node.getClass() + "  " + String.format("0x%x", System.identityHashCode(node)));
                    }
                    System.out.println("-----------------");
                    System.out.println(nodes);
                }
            }
        }
    }

    public ModifiedNode canASTNodeBeAnnotated(ASTNode node, AnnotationWrapper annotation) {
        ModifiedNode modifiedNode = null;
        String annotationTarget = annotation.getTarget();
        if (annotation.getName().equals("UtilityClass")) {
            List<ASTNode> subNodes = getChildrenNodes(node);
            for (ASTNode subNode : subNodes) {
                if (subNode instanceof MethodInvocation) {
                    String methodName = ((MethodInvocation) subNode).getName().getIdentifier();
                    if (methodName.equals("hashCode") || methodName.equals("finalize")) {
                        if (((MethodInvocation) subNode).arguments().size() == 0) {
                            return null;
                        }
                    }
                    if (methodName.equals("equals")) {
                        if (((MethodInvocation) subNode).arguments().size() == 1) {
                            return null;
                        }
                    }
                }
                if (subNode instanceof MethodDeclaration) {
                    String methodName = ((MethodDeclaration) subNode).getName().getIdentifier();
                    if (methodName.equals("hashCode") || methodName.equals("finalize")) {
                        if (((MethodDeclaration) subNode).parameters().size() == 0) {
                            return null;
                        }
                    }
                    if (methodName.equals("equals")) {
                        if (((MethodDeclaration) subNode).parameters().size() == 1
                                && ((MethodDeclaration) subNode).parameters().get(0) instanceof SingleVariableDeclaration
                                && ((SingleVariableDeclaration) ((MethodDeclaration) subNode).parameters().get(0)).getType().toString().equals("Object")) {
                            return null;
                        }
                    }
                }
            }
        }
        if (node instanceof TypeDeclaration && annotationTarget.contains("TYPE")) {
            TypeDeclaration newNode = (TypeDeclaration) node;
            if (newNode.isInterface()) {
                return null;
            }
            if (annotation.getName().equals("UtilityClass") && hasConstructor(newNode)) {
                return null;
            }
            if (annotation.getName().equals("NoArgsConstructor") && hasNoArgsConstructor(newNode)) {
                return null;
            }
            if (annotation.getName().equals("AllArgsConstructor") && hasAllArgsConstructor(newNode)) {
                return null;
            }
            if(annotation.getName().equals("Log4j") && newNode.getParent() instanceof TypeDeclaration) {
                boolean ok = false;
                for(Object modifier : newNode.modifiers()) {
                    if(modifier.toString().contains("static")) {
                        ok = true;
                        break;
                    }
                }
                if(!ok) {
                    return null;
                }
            }
            if (annotation.getName().equals("StandardException")) {
                if (newNode.getSuperclassType() == null) {
                    return null;
                }
                Type superClass = newNode.getSuperclassType();
                if (superClass instanceof SimpleType) {
                    if (!((SimpleType) superClass).getName().getFullyQualifiedName().endsWith("Exception")) {
                        return null;
                    }
                } else {
                    if (!(superClass instanceof ParameterizedType)) {
                        System.err.println("Super Class is not SimpleName or ParameterizedType!");
                        System.err.println("Error filPath: " + this.filePath);
                        System.exit(-1);
                    }
                }
            }
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), TypeDeclaration.MODIFIERS2_PROPERTY);
        }
        if (node instanceof FieldDeclaration && annotationTarget.contains("FIELD")) {
            FieldDeclaration newNode = (FieldDeclaration) node;
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), FieldDeclaration.MODIFIERS2_PROPERTY);
        }
        if (node instanceof MethodDeclaration && annotationTarget.contains("METHOD") && !((MethodDeclaration) node).isConstructor()) {
            MethodDeclaration newNode = (MethodDeclaration) node;
            if (annotation.getName().equals("Synchronized")) {
                if(newNode.getName().getIdentifier().equals("finalize")) {
                    return null;
                }
                TypeDeclaration type = getClassOfASTNode(newNode);
                if (type.isInterface()) {
                    return null;
                }
            }
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), MethodDeclaration.MODIFIERS2_PROPERTY);
        }
        if (node instanceof SingleVariableDeclaration && annotationTarget.contains("PARAMETER")) {
            SingleVariableDeclaration newNode = (SingleVariableDeclaration) node;
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), SingleVariableDeclaration.MODIFIERS2_PROPERTY);
        }
        if (node instanceof MethodDeclaration && annotationTarget.contains("CONSTRUCTOR") && ((MethodDeclaration) node).isConstructor()) {
            MethodDeclaration newNode = (MethodDeclaration) node;
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), MethodDeclaration.MODIFIERS2_PROPERTY);
        }
        if (node instanceof VariableDeclarationStatement && annotationTarget.contains("LOCAL_VARIABLE")) {
            VariableDeclarationStatement newNode = (VariableDeclarationStatement) node;
            if (annotation.getName().equals("Cleanup")) {
                if (newNode.getType().isPrimitiveType() || newNode.getType().isArrayType() || newNode.getType().toString().equals("Object")
                        || newNode.getType() instanceof ParameterizedType || newNode.getType().toString().equals("Runnable")) {
                    return null;
                }
                VariableDeclarationFragment newFragment = (VariableDeclarationFragment) newNode.fragments().get(0);
                if(newFragment.getInitializer() == null) {
                    return null;
                }
            }
            modifiedNode = new ModifiedNode(node, newNode.modifiers(), VariableDeclarationStatement.MODIFIERS2_PROPERTY);
        }
        if (node instanceof ReturnStatement && annotationTarget.contains("TYPE_USE")) {
            ReturnStatement newNode = (ReturnStatement) node;
            if (newNode.getExpression() instanceof ExpressionMethodReference) {
                modifiedNode = new ModifiedNode(newNode.getExpression(), new ArrayList<>(), null);
            }
        }
        return modifiedNode;
    }

    public void removePureConstructor(List<MethodDeclaration> methods) {
        if(methods.size() > 0) {
            for (MethodDeclaration method : methods) {
                this.astRewrite.remove(method, null);
            }
            this.rewriteJavaCode();
            this.writeToJavaFile();
        }
    }

    public List<TypeDeclaration> getTypes() {
        return this.types;
    }

    public String getFolderPath() {
        return this.folderPath;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getCode() {
        return this.document.get();
    }

    public AST getAST() {
        return ast;
    }

    public ASTRewrite getAstRewrite() {
        return this.astRewrite;
    }

    public AnnotationWrapper getInsertedAnnotationWrapper() {
        return this.insertedAnnotationWrapper;
    }

    public Annotation wrapAnnotationNode(AnnotationWrapper annotationWrapper, TypeWrapper mutant) {
        Annotation newAnnotationNode = null;
        boolean matched = false;
        if (annotationWrapper.getAnnotationName().equals("Order")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            NumberLiteral number = mutant.getAST().newNumberLiteral("0");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(number);
        }
        if (annotationWrapper.getAnnotationName().equals("Generated")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("com.mock.Test");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(literal);
        }
        if (annotationWrapper.getAnnotationName().equals("DecimalMin") || annotationWrapper.getAnnotationName().equals("DecimalMax")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("0.0");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(literal);
        }
        if (annotationWrapper.getAnnotationName().equals("HandlesTypes")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("WebApplicationInitializer.class");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(literal);
        }
        if (annotationWrapper.getAnnotationName().equals("GuardedBy")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("this");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(literal);
        }
        if (annotationWrapper.getAnnotationName().equals("RolesAllowed")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("test");
            ((SingleMemberAnnotation) newAnnotationNode).setValue(literal);
        }
        if (annotationWrapper.getAnnotationName().equals("Pattern")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newNormalAnnotation();
            MemberValuePair pair = mutant.getAST().newMemberValuePair();
            pair.setName(mutant.getAST().newSimpleName("regexp"));
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("([0-9]{0,10}\\.?[0-9]{0,0}?)?");
            pair.setValue(literal);
            ((NormalAnnotation) newAnnotationNode).values().add(pair);
        }
        if (annotationWrapper.getAnnotationName().equals("DeclareRoles")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newSingleMemberAnnotation();
            ArrayInitializer initializer = mutant.getAST().newArrayInitializer();
            StringLiteral literal = mutant.getAST().newStringLiteral();
            literal.setLiteralValue("Tester");
            initializer.expressions().add(literal);
            ((SingleMemberAnnotation) newAnnotationNode).setValue(initializer);
        }
        if (annotationWrapper.getAnnotationName().equals("ApiResponse")) {
            matched = true;
            newAnnotationNode = mutant.getAST().newNormalAnnotation();
            MemberValuePair pair1 = mutant.getAST().newMemberValuePair();
            MemberValuePair pair2 = mutant.getAST().newMemberValuePair();
            pair1.setName(mutant.getAST().newSimpleName("regexp"));
            pair2.setName(mutant.getAST().newSimpleName("message"));
            NumberLiteral literal1 = mutant.getAST().newNumberLiteral();
            StringLiteral literal2 = mutant.getAST().newStringLiteral();
            literal1.setToken("200");
            pair1.setValue(literal1);
            literal2.setLiteralValue("success");
            pair2.setValue(literal2);
            ((NormalAnnotation) newAnnotationNode).values().add(pair1);
            ((NormalAnnotation) newAnnotationNode).values().add(pair2);
        }
        if (!matched) {
            newAnnotationNode = mutant.getAST().newMarkerAnnotation();
        }
        if (newAnnotationNode == null) {
            System.err.println("Unexpected Null!");
            System.exit(-1);
        }
        newAnnotationNode.setTypeName(mutant.getAST().newSimpleName(annotationWrapper.getName()));
        return newAnnotationNode;
    }

    public static boolean isAnnotationFile(String path) {
        if (!path.contains(".java")) {
            return false;
        }
        File targetFile = new File(path);
        Document document = null;
        try {
            document = new Document(FileUtils.readFileToString(targetFile, "UTF-8"));
        } catch (IOException e) {
            System.err.println("Fail to parse file: " + targetFile);
            e.printStackTrace();
        }
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setCompilerOptions(compilerOptions);
        parser.setSource(document.get().toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        cu.recordModifications();
        List<ASTNode> initTypes = (List<ASTNode>) cu.types();
        if (initTypes == null || initTypes.size() == 0) {
            return false;
        }
        boolean isAnnotation = true;
        for (ASTNode type : initTypes) {
            if (type instanceof TypeDeclaration) {
                isAnnotation = false;
            }
        }
        return isAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationWrapper) {
            return this.filePath.equals(((AnnotationWrapper) o).getFilePath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.filePath.hashCode();
    }

    @Override
    public String toString() {
        return this.filePath;
    }

}
